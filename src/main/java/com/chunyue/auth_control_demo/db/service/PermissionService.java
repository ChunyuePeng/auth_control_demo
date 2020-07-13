package com.chunyue.auth_control_demo.db.service;

import java.util.List;
import java.util.Set;

public interface PermissionService {
    Set<String> getPermissionsByIds(List<Integer> ids);
}
