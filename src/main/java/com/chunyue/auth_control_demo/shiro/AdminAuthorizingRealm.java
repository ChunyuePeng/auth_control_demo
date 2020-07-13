package com.chunyue.auth_control_demo.shiro;


import com.chunyue.auth_control_demo.db.domain.Admin;
import com.chunyue.auth_control_demo.db.service.AdminService;
import com.chunyue.auth_control_demo.db.service.PermissionService;
import com.chunyue.auth_control_demo.db.service.RolePermissionService;
import com.chunyue.auth_control_demo.db.service.RoleService;
import com.chunyue.auth_control_demo.service.AuthorizationInfoCacheManager;
import com.chunyue.auth_control_demo.util.bcrypt.BCryptPasswordEncoder;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdminAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private PermissionService permissionService;

    //获取授权信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        Admin admin = (Admin) getAvailablePrincipal(principals);
        SimpleAuthorizationInfo authorizationInfo = AuthorizationInfoCacheManager.getAuthorizationInfo(admin.getId());
        if (authorizationInfo!=null) {
            return authorizationInfo;
        }
        else {
            Integer[] roleIds = admin.getRoleIds();
            Set<String> roles = roleService.queryByIds(roleIds);
            Set<Integer> permissionsIds = rolePermissionService.queryByRoleIds(roleIds);
            Set<String> permissions = permissionService.getPermissionsByIds(new ArrayList<>(permissionsIds));
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.setRoles(roles);
            info.setStringPermissions(permissions);
            AuthorizationInfoCacheManager.addAuthorizationInfo(admin.getId(),info);
            return info;
        }
    }

    //获取身份验证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //可以将登录次数限制放在这里


        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        if (StringUtils.isEmpty(username)) {
            throw new AccountException("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            throw new AccountException("密码不能为空");
        }

        List<Admin> adminList = adminService.findAdmin(username);
        Assert.state(adminList.size() < 2, "同一个用户名存在两个账户");
        if (adminList.size() == 0) {
            throw new UnknownAccountException("无此用户");
        }
        Admin admin = adminList.get(0);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, admin.getPassword())) {
            throw new UnknownAccountException("用户名和密码不匹配");
        }

        return new SimpleAuthenticationInfo(admin, password, getName());
    }

}
