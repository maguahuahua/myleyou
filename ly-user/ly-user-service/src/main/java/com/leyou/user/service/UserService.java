package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.util.CodecUtils;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author chenxm
 * @date 2020/7/19 - 23:47
 */

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    static final String KEY_PREFIX = "user:code:phone:";

    /**
     * 校验用户名、手机号存不存在
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        User record = new User();
        //判断数据类型
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        return userMapper.selectCount(record) == 0;
    }

    /**
     * 发送验证码短信
     * @param phone
     */
    //数据抽取到配置文件，不要写死
    public void sendCode(String phone) {
        String key = KEY_PREFIX + phone;
        String code = NumberUtils.generateCode(6);
        HashMap<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);

        //发送验证码,会被sms监听到，从而发信息
//        amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code", msg);

        //保存验证码
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
    }


    /**
     * - 1）校验短信验证码
     * - 2）生成盐
     * - 3）对密码加密
     * - 4）写入数据库
     * - 5）删除Redis中的验证码
     * @param user
     * @param code
     * @return
     */
    public Boolean register(User user, String code) {
        // 校验短信验证码
        String cacheCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        System.out.println("cacheCode = " + cacheCode);
        if (!StringUtils.equals(code, code)) {    //是cacheCode，但是没短信。
            return false;
        }

        // 生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        // 对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        // 强制设置不能指定的参数为null
        user.setId(null);
        user.setCreated(new Date());
        // 添加到数据库
        boolean b = this.userMapper.insertSelective(user) == 1;

        if (b) {
            // 注册成功，删除redis中的记录
            this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
        }
        return b;
    }

    /**
     * 查询功能，根据参数中的用户名和密码查询指定用户
     * @param username
     * @param password
     * @return
     */
    public User queryUser(String username, String password) {
        // 查询
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        // 校验用户名
        if (user == null) {
            return null;
        }
        // 校验密码
        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
            return null;
        }
        // 用户名密码都正确
        return user;
    }
}


