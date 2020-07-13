package com.chunyue.auth_control_demo.db.service.impl;

import com.chunyue.auth_control_demo.db.dao.RoleMapper;
import com.chunyue.auth_control_demo.db.domain.Role;
import com.chunyue.auth_control_demo.db.domain.RoleExample;
import com.chunyue.auth_control_demo.db.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    RoleMapper roleMapper;
    @Override
    public Set<String> queryByIds(Integer[] roleIds) {
        List<Integer> ids = Stream.of(roleIds).collect(Collectors.toList());
        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids).andDeletedEqualTo(false);
        Role.Column column[] = {Role.Column.name};
        List<Role> roles = roleMapper.selectByExampleSelective(example, column);
        Set<String> result = new HashSet<>();
        for (Role role : roles) {
            result.add(role.getName());
        }
        return result;
    }
}
