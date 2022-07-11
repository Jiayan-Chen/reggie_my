package com.chenjiayan.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.chenjiayan.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 因为有通配符，所以无法直接比较，要用到 路径匹配器（支持通配符）
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    String[] uris = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**"
    };
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        log.info("拦截到uri是{}",uri);
        Boolean check = checkURI(uri);
        // 如果匹配成功直接放行
        if(check){
            log.info("匹配成功，放行");
            filterChain.doFilter(request,response);
            return ;
        }
        // 如果已经登录，直接放行
        Long id = (Long) request.getSession().getAttribute("employee");
        if(id!=null){
            log.info("用户已登录，放行");
            filterChain.doFilter(request,response);
            return ;
        }
        // 未登录
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 进行路径匹配
     * @param uri
     * @return
     */
    private Boolean checkURI(String uri){
        for (String s : uris) {
            boolean b = PATH_MATCHER.match(s, uri);
            if(b){
                return true;
            }
        }
        return false;
    }
}
