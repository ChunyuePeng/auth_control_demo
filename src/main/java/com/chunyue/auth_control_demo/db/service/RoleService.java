package com.chunyue.auth_control_demo.db.service;

import java.util.Set;

public interface RoleService {
    Set<String> queryByIds(Integer[] roleIds);
}
