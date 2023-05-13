package com.anyi.reggie.service;

import com.anyi.reggie.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-05-25
 */
public interface UserService extends IService<User> {
    // 发送邮箱验证码
    Boolean sendMsg(User user, HttpSession session) throws MessagingException;
    // 移动端用户登录
    User login(Map<String, String> map, HttpSession session);
}
