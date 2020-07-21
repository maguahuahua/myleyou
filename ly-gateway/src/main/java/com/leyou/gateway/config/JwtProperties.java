package com.leyou.gateway.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author chenxm
 * @date 2020/7/20 - 16:07
 */

@Slf4j
@ConfigurationProperties(prefix = "leyou.jwt")
@Data
public class JwtProperties {
    private String pubKeyPath;// 公钥

    private String cookieName;

    private PublicKey publicKey;

    /**
     * @PostContruct：在构造方法执行之后执行该方法,对象一旦实例化后，就读取公钥和私钥
     */
    @PostConstruct
    public void init() throws Exception {
        //读取公钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}