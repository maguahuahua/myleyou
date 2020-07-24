package com.leyou.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenxm
 * @date 2020/7/21 - 19:03
 */

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private JwtProperties jwtProperties;

    @Autowired
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    //构造注入了自己在MvcConfig中new的
    public LoginInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            //解析token
            UserInfo user = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //传递user
//            request.setAttribute("user", user);   //不推荐
            //用线程存，底层是map，键是线程本身，设置。取值的时候也是
            //线程内共享，因此请求到达`Controller后可以共享User
            tl.set(user);
            //放行
            return true;
        } catch (Exception e) {
            log.error("【购物车】： 解析用户身份失败");
            return false;
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //用完数据清空
        tl.remove();
    }


    public static UserInfo getUserInfo(){
        return tl.get();
    }
}
