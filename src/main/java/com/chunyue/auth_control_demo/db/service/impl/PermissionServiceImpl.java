package com.chunyue.auth_control_demo.db.service.impl;

import com.chunyue.auth_control_demo.db.dao.PermissionMapper;
import com.chunyue.auth_control_demo.db.domain.Permission;
import com.chunyue.auth_control_demo.db.domain.PermissionExample;
import com.chunyue.auth_control_demo.db.domain.Role;
import com.chunyue.auth_control_demo.db.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {
    Permission.Column column[] = {Permission.Column.permission};
    @Resource
    PermissionMapper mapper;


    @Override
    public Set<String> getPermissionsByIds(List<Integer> ids) {
        PermissionExample example = new PermissionExample();
        PermissionExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        List<Permission> permissions = mapper.selectByExampleSelective(example,column);

        Set<String> permissionsStr = new HashSet<>();
        for (Permission permission : permissions) {
            permissionsStr.add(permission.getPermission());
        }
        return permissionsStr;
    }
}
