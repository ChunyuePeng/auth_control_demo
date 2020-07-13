package com.chunyue.auth_control_demo.db.service.impl;

import com.chunyue.auth_control_demo.db.dao.RolePermissionMapper;
import com.chunyue.auth_control_demo.db.domain.RolePermission;
import com.chunyue.auth_control_demo.db.domain.RolePermissionExample;
import com.chunyue.auth_control_demo.db.service.RolePermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {
    RolePermission.Column column[] = {RolePermission.Column.permissionId};
    @Resource
    RolePermissionMapper mapper;
    @Override
    public Set<Integer> queryByRoleIds(Integer[] roleIds) {
        List<Integer> ids = Stream.of(roleIds).collect(Collectors.toList());
        RolePermissionExample example = new RolePermissionExample();
        RolePermissionExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdIn(ids);
        List<RolePermission> rolePermissions = mapper.selectByExampleSelective(example, column);

        Set<Integer> permissionIds = new HashSet<>();
        for (RolePermission rolePermission : rolePermissions) {
            permissionIds.add(rolePermission.getPermissionId());
        }
        return permissionIds;
    }
}
