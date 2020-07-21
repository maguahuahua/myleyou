package com.leyou.user.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Pattern;
import tk.mybatis.mapper.annotation.KeySql;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author chenxm
 * @date 2020/7/19 - 23:39
 */
/**
 * @validation 校验格式
 * 虽然实现了注册，但是服务端并没有进行数据校验，而前端的校验是很容易被有心人绕过的。所以我们必须在后台添加数据校验功能：
 * 我们这里会使用Hibernate-Validator框架完成数据校验：
 */

@Data
@Table(name = "tb_user")
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Length(min = 4, max = 30, message = "用户名只能在4~30位之间")
    private String username;// 用户名

    @Length(min = 4, max = 30, message = "密码只能在4~30位之间")
    @JsonIgnore
    private String password;// 密码

    @Pattern(regexp = "^1[35678]\\d{9}$", message = "手机号格式不正确")
    private String phone;// 电话

    private Date created;// 创建时间

    @JsonIgnore
    private String salt;// 密码的盐值
}
