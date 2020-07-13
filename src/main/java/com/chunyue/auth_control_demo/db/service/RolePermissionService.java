package com.chunyue.auth_control_demo.db.service;

import java.util.Set;

public interface RolePermissionService {
    Set<Integer> queryByRoleIds(Integer[] roleIds);

}
