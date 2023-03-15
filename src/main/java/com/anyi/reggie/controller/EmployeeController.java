package com.anyi.reggie.controller;


import cn.hutool.crypto.SecureUtil;
import com.anyi.reggie.common.R;
import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.entity.Employee;
import com.anyi.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author
 * @since
 */
@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;
    // 登录
    @PostMapping("/login")
    public R login(HttpServletRequest request, @RequestBody Employee employee){

        // 1. 查询数据库中是否有该用户
        //Employee user = employeeService.getOne(new QueryWrapper<Employee>().eq("username", employee.getUsername()));
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee user = employeeService.getOne(queryWrapper);
        if (user == null){
            return R.error("登录失败");
        }


        // 2. 判断密码是否 正确
        String password = SecureUtil.md5(employee.getPassword());
        if (!password.equals(user.getPassword())){
            return R.error("登录失败");
        }

        // 3. 判断用户是否被禁用
        if (user.getStatus() == 0){
            return R.error("登录失败");
        }
        // 4. 返回用户信息，并保存到session中
        request.getSession().setAttribute("employee", user);
        // 将数据写入到threadLocal中
        return R.success(user);
    }
    // 退出登录
    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success(null);
    }

    /**
     * 添加员工
     * @return
     */
    @PostMapping
    public R addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        // 1. 查询员工是否储存
        Employee one = employeeService.getOne(new QueryWrapper<Employee>().eq("username", employee));
        if (one != null){
            return  R.error( employee.getUsername() + "用户已经存在");
        }
        // 2. 填写创建用户信息，填写初始化密码
        Long em = (Long)request.getSession().getAttribute("employee");
        UserContext.setUserId(em);
        employee.setPassword(SecureUtil.md5("123456"));
        //创建这个记录的用户id
        //employee.setCreateUser(em.getId());
        //更新这条记录的用户id
         //employee.setUpdateUser(em.getId());
        // 3.存入到数据库
        employeeService.save(employee);
        log.info("新增员工，员工信息：{}", employee.toString());
        return R.success("添加用户成功");
    }

    /**
     * 修改员工信息
     * @param
     * @param employee
     * @return
     */
//    @PutMapping
//    public R<String> update(@RequestBody Employee employee){
//        log.info(employee.toString());
//        Long id = Thread.currentThread().getId();
//        log.info("当前线程id为：{}",id);
//        employeeService.updateById(employee);
//        return R.success("员工信息修改成功！");
//    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
//    @GetMapping("/page")
//    public R page(int page ,int pageSize , String name){
//        log.info(UserContext.getUserId().toString());
//        // 创建分页
//        Page<Employee> pageInfo = new Page<>(page, pageSize);
//        QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
//        employeeQueryWrapper.orderByDesc("create_time");
//        if(name !=null){
//            employeeQueryWrapper.like("name", name);
//        }
//        employeeService.page(pageInfo, employeeQueryWrapper);
//        return R.success(pageInfo);
//    }

    /**
     * 用Mybatis-plus完成分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }



    /**
     * 更改账号状态
     * @param employee
     * @return
     */
    @PutMapping
    public R changeStatus(@RequestBody Employee employee){
        employeeService.saveOrUpdate(employee);
        return R.success("更新成功");
    }

    /**
     * 根据id 获取员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R getEmployeeById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息！");
    }
}

