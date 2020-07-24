package com.leyou.gateway.filter;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chenxm
 * @date 2020/7/20 - 21:49
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;    //前置过滤器的类型
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;  //过滤器顺序
    }

    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest req = ctx.getRequest();
        // 获取路径
        String requestURI = req.getRequestURI();
        // 判断白名单
        // 遍历允许访问的路径
        for (String path : filterProperties.getAllowPaths()) {
            // 然后判断是否是符合
            if(requestURI.startsWith(path)){
                return false;    //不过滤，即放行
            }
        }
        return true;           //不在允许的路径列表，走过滤器
    }


    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = context.getRequest();

        // 获取token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        // 校验
        try {
            // 校验通过什么都不做，即放行
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //TODO 校验权限,得到用户信息，得到用户权限，根据request中url判断是是否具有对应权限
        } catch (Exception e) {
            // 解析token失败，未登录，拦截，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }
}