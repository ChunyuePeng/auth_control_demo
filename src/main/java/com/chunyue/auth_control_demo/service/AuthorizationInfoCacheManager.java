package com.chunyue.auth_control_demo.service;

import org.apache.shiro.authz.SimpleAuthorizationInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于缓存权限信息
 */
public class AuthorizationInfoCacheManager {
    private static Map<Integer, SimpleAuthorizationInfo> map = new HashMap<>();
    public static boolean addAuthorizationInfo(Integer id,SimpleAuthorizationInfo info){
        try {
            map.put(id,info);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static SimpleAuthorizationInfo getAuthorizationInfo(Integer id){
        return map.get(id);
    }

    public static boolean removeAuthorizationInfo(Integer id){
        try {
            map.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
