//package com.anyi.reggie.filter;
//
//import cn.hutool.json.JSONUtil;
//import com.anyi.reggie.common.R;
//import com.anyi.reggie.common.UserContext;
//import com.anyi.reggie.entity.Employee;
//import com.anyi.reggie.entity.User;
//import com.sun.prism.impl.BaseContext;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.AntPathMatcher;
//import springfox.documentation.spring.web.json.Json;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author 安逸i
// * @version 1.0
// */
//@WebFilter(filterName = "userFilter",urlPatterns = "/*")
//@Slf4j
//public class UserFilter implements Filter {
//    //路径匹配器，支持通配符
//    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest servletRequest = (HttpServletRequest) request;
//        HttpServletResponse servletResponse = (HttpServletResponse) response;
//        //本次请求
//        String url =servletRequest.getRequestURI();
//        log.info("拦截到请求{}", url);
//        log.info(servletRequest.getRequestURI());
//        // 设置不要拦截的请求
//        String[] urls = new String[]{
//                "/employee/login",
//                "/employee/layout",
//                "/employee/page",
//                "/backend/**",
//                "/front/**",
//                "/swagger-ui.html",
//                "/user/code",
//                "/user/login"
//        };
//        //判断本次请求是否需要处理，如果不需要处理则直接放行
//        if (match(servletRequest.getRequestURI(),urls)){
//            log.info("本次请求{}不需要处理" + url);
//            Long id = Thread.currentThread().getId();
//            log.info("当前线程id为：{}",id);
//            chain.doFilter(servletRequest,servletResponse);
//            return;
//        }
//        // 判断用户和管理员是否已经登录
//        User user = (User)servletRequest.getSession().getAttribute("user");
//        Employee employee = (Employee)servletRequest.getSession().getAttribute("employee");
//
//        // 需要验证的请求
//        if (user != null){
//            UserContext.setUserId(user.getId());
//            chain.doFilter(servletRequest,servletResponse);
//        }else if (employee !=null){
//            UserContext.setUserId(employee.getId());
//            chain.doFilter(servletRequest,servletResponse);
//        }else {
//            response.getWriter().write(JSONUtil.toJsonStr(R.error("NOTLOGIN")));
//
//        }
//    }
//
//    /**
//     *路径匹配，检查此次请求是否放行
//     * @param url
//     * @param urls
//     * @return
//     */
//    // 匹配url
//    public boolean match(String url,String[] urls){
//        for (String item : urls) {
//            if (PATH_MATCHER.match(item, url)){
//                return true;
//            }
//        }
//        return false;
//    }
//}
