package com.chunyue.auth_control_demo.db.service;

import java.util.Set;

public interface PermissionService {

    Set<String> queryByRoleId(Integer roleId);

    Set<String> queryByRoleIds(Integer[] roleIds);
}
