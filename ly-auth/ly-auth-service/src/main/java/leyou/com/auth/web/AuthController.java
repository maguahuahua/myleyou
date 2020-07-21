package leyou.com.auth.web;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import leyou.com.auth.config.JwtProperties;
import leyou.com.auth.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenxm
 * @date 2020/7/20 - 16:27
 */
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties prop;

    /**
     * 登录授权
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> authentication(
            @RequestParam("username") String username, @RequestParam("password") String password,
            HttpServletRequest request, HttpServletResponse response) {
        // 登录校验
        String token = this.authService.authentication(username, password);
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        CookieUtils.setCookie(request, response, prop.getCookieName(),
                token, prop.getCookieMaxAge(), true);
        return ResponseEntity.ok().build();
    }


    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(
            @CookieValue("LY_TOKEN") String token,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            //解析token信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //刷新token，重新生成token
            String newToken = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
            //写入cookie
            CookieUtils.setCookie(request, response, prop.getCookieName(),
                    newToken, prop.getCookieMaxAge(), true);


            // 解析成功返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // token过期，或者被篡改，响应403
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }
}