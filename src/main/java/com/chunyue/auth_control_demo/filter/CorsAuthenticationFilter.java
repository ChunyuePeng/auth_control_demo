package com.chunyue.auth_control_demo.filter;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 此filter的作用是解决跨域时OPTIONS请求被拦截而导致权限验证问题
 */
public class CorsAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (request instanceof HttpServletRequest){
            if (((HttpServletRequest)request).getMethod().toUpperCase().equals("OPTIONS")){
                return true;
            }
        }
        if (request.getAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID)!=null)
            return true;
        return false;
    }
}
