package com.chunyue.auth_control_demo.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

public class PermissionUtil {

    public static List<Permission> listPermission(ApplicationContext context, String basicPackage) {
        Map<String, Object> map = context.getBeansWithAnnotation(Controller.class);
        List<Permission> permissions = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object bean = entry.getValue();
            if (!StringUtils.contains(ClassUtils.getPackageName(bean.getClass()), basicPackage)) {
                continue;
            }

            Class<?> clz = bean.getClass();
            Class controllerClz = clz.getSuperclass();
            RequestMapping clazzRequestMapping = AnnotationUtils.findAnnotation(controllerClz, RequestMapping.class);
            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(controllerClz, RequiresPermissions.class);
            for (Method method : methods) {
                RequiresPermissions requiresPermissions = AnnotationUtils.getAnnotation(method,
                        RequiresPermissions.class);
                if (requiresPermissions == null) {
                    continue;
                }

                String api = "";
                if (clazzRequestMapping != null) {
                    api = clazzRequestMapping.value()[0];
                }

                //post请求
                PostMapping postMapping = AnnotationUtils.getAnnotation(method, PostMapping.class);
                if (postMapping != null) {
                    api = "POST " + api + postMapping.value()[0];
                    Permission permission = new Permission();
                    permission.setRequiresPermissions(requiresPermissions);
                    permission.setApi(api);
                    permissions.add(permission);
                    continue;
                }
                //get请求
                GetMapping getMapping = AnnotationUtils.getAnnotation(method, GetMapping.class);
                if (getMapping != null) {
                    api = "GET " + api + getMapping.value()[0];
                    Permission permission = new Permission();
                    permission.setRequiresPermissions(requiresPermissions);
                    permission.setApi(api);
                    permissions.add(permission);
                    continue;
                }
                //delete请求
                DeleteMapping deleteMapping = AnnotationUtils.getAnnotation(method, DeleteMapping.class);
                if (deleteMapping != null){
                    api = "DELETE " + deleteMapping.value()[0];
                    Permission permission = new Permission();
                    permission.setRequiresPermissions(requiresPermissions);
                    permission.setApi(api);
                    permissions.add(permission);
                    continue;
                }
                //put请求
                PutMapping putMapping = AnnotationUtils.getAnnotation(method, PutMapping.class);
                if (putMapping != null) {
                    api = "PUT " + api +putMapping.value()[0];
                    Permission permission = new Permission();
                    permission.setRequiresPermissions(requiresPermissions);
                    permission.setApi(api);
                    permissions.add(permission);
                    continue;
                }
                throw new RuntimeException("目前支持的请求为post、get、delete、put");
            }
        }
        return permissions;
    }
}
