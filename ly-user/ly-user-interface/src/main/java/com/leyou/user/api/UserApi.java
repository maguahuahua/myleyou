package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author chenxm
 * @date 2020/7/20 - 16:40
 */

@RequestMapping           //("user")
public interface UserApi {

    @GetMapping("query")
    User queryUser(@RequestParam("username") String username, @RequestParam("password") String password);
}