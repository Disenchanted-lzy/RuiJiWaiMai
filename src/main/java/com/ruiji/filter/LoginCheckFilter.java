package com.ruiji.filter;


import com.alibaba.fastjson.JSON;
import com.ruiji.common.BaseContext;
import com.ruiji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        log.info("拦截请求:{}",httpRequest.getRequestURI());

        String requestURI = httpRequest.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        boolean check = check(requestURI,urls);
        if(check){
            chain.doFilter(request,response);
            return;
        }

        if(httpRequest.getSession().getAttribute("employee")!=null){
            Long empId = (Long) httpRequest.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            chain.doFilter(request,response);
            return;
        }

        if(httpRequest.getSession().getAttribute("user")!=null){
            Long userId = (Long) httpRequest.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            chain.doFilter(request,response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    @Override
    public void destroy() {

    }

    public boolean check(String url,String[] urls){
        for (String s : urls) {
           boolean res =  PATH_MATCHER.match(s,url);
           if(res) return true;
        }
        return false;
    }
}
