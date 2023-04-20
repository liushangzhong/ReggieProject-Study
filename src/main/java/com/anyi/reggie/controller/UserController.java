package com.anyi.reggie.controller;


import cn.hutool.core.util.RandomUtil;
import com.anyi.reggie.common.R;
import com.anyi.reggie.dto.LoginDto;
import com.anyi.reggie.entity.User;
import com.anyi.reggie.service.UserService;
import com.anyi.reggie.utils.ValidaterUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-05-25
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    //注入Redis对象
    @Resource
    private RedisTemplate myRedisTemplate;

    @Resource
    private UserService userService;



    /**
     * 获取验证码
     *
     * @param phone
     * @return
     */
    @GetMapping("/code")
    public R getCode(String phone) {
        String code = RandomUtil.randomNumbers(6);
        //将验证码存入到Redis中
        myRedisTemplate.opsForValue().set("phone", code, 5, TimeUnit.MINUTES);
        return R.success("发送验证码成功！");
    }


    // 发送邮箱验证码
    @PostMapping("/sendMsg")
    public R sendMsg(@RequestBody User user, HttpSession session) throws MessagingException {
        if (userService.sendMsg(user, session)) {
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }


    // 移动端用户登录登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {
        User user = userService.login(map, session);
        return R.success(user);
    }


    @PostMapping("/login1")
    public R login(@RequestBody LoginDto loginDto, HttpSession session){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        User user = userService.getOne(wrapper.eq(User::getEmail, loginDto.getEmail()));
        //从缓存中拿到验证码
        String  is = (String)myRedisTemplate.opsForValue().get("phone");
        if (loginDto.getCode().equals(is)){
            User curUser = new User();
            if (user == null){
                curUser.setEmail(loginDto.getEmail());
                curUser.setStatus(1);
                userService.save(curUser);
            }else {
                curUser = user;
            }
            session.setAttribute("user", curUser);
            //如果用户登录成功，就删除redis中缓存的验证
            myRedisTemplate.delete("phone");
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success(null);
    }


}

