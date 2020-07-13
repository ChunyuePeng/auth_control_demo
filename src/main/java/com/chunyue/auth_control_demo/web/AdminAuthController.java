package com.chunyue.auth_control_demo.web;

import com.chunyue.auth_control_demo.db.domain.Admin;
import com.chunyue.auth_control_demo.db.service.AdminService;
import com.chunyue.auth_control_demo.db.service.PermissionService;
import com.chunyue.auth_control_demo.db.service.RolePermissionService;
import com.chunyue.auth_control_demo.db.service.RoleService;
import com.chunyue.auth_control_demo.service.AuthorizationInfoCacheManager;
import com.chunyue.auth_control_demo.service.LogHelper;
import com.chunyue.auth_control_demo.util.*;
import org.apache.catalina.security.SecurityUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/admin/auth")
@Validated
public class AdminAuthController {
    private final Log logger = LogFactory.getLog(AdminAuthController.class);

    @Autowired
    private LogHelper logHelper;
    @Autowired
    AdminService adminService;
    @Autowired
    RoleService roleService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    RolePermissionService rolePermissionService;

    /*
     *  { username : value, password : value }
     */
    @PostMapping("/login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            logHelper.logAuthFail("登录", "用户帐号或密码不正确");
            return ResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_ACCOUNT, uae.getMessage());
        } catch (LockedAccountException lae) {
            logHelper.logAuthFail("登录", "用户帐号已锁定不可用");
            return ResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_ACCOUNT, "用户帐号已锁定不可用");

        } catch (AuthenticationException ae) {
            logHelper.logAuthFail("登录", "认证失败");
            return ResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_ACCOUNT, "认证失败");
        }

        currentUser = SecurityUtils.getSubject();
        Admin admin = (Admin) currentUser.getPrincipal();
        adminService.updateById(admin);

        logHelper.logAuthSucceed("登录");

        // userInfo
        Map<String, Object> adminInfo = new HashMap<String, Object>();
        adminInfo.put("nickName", admin.getUsername());
        //return result
        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", currentUser.getSession().getId());
        result.put("adminInfo", adminInfo);
        return ResponseUtil.ok(result);
    }

    /*
     *
     */
    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();

        logHelper.logAuthSucceed("退出");
        currentUser.logout();
        return ResponseUtil.ok();
    }


    @RequiresAuthentication
    @GetMapping("/info")
    public Object info() {
        Subject currentUser = SecurityUtils.getSubject();
        Admin admin = (Admin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());

        Integer[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.queryByIds(roleIds);
        Set<Integer> permissionIds = rolePermissionService.queryByRoleIds(roleIds);
        Set<String> permissions = permissionService.getPermissionsByIds(new ArrayList<>(permissionIds));
        //将权限信息缓存到AuthorizationInfoCacheManager中
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        AuthorizationInfoCacheManager.addAuthorizationInfo(admin.getId(),info);
        data.put("roles", roles);
        // NOTE
        // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
        data.put("perms", toApi(permissions));
        return ResponseUtil.ok(data);
    }

    @Autowired
    private ApplicationContext context;
    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toApi(Set<String> permissions) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            String packageName = AdminAuthController.class.getPackage().getName();
            List<Permission> systemPermissions = PermissionUtil.listPermission(context, packageName);
            for (Permission permission : systemPermissions) {
                String perm = permission.getRequiresPermissions().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            apis.add(api);
            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
            }
        }
        return apis;
    }

    @GetMapping("/401")
    public Object page401() {
        return ResponseUtil.unlogin();
    }

    @GetMapping("/index")
    public Object pageIndex() {
        return ResponseUtil.ok();
    }

    @GetMapping("/403")
    public Object page403() {
        return ResponseUtil.unauthz();
    }
}
