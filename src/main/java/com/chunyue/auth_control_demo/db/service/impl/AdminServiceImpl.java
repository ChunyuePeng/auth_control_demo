package com.chunyue.auth_control_demo.db.service.impl;

import com.chunyue.auth_control_demo.db.dao.AdminMapper;
import com.chunyue.auth_control_demo.db.domain.Admin;
import com.chunyue.auth_control_demo.db.domain.AdminExample;
import com.chunyue.auth_control_demo.db.service.AdminService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Resource
    AdminMapper mapper;

    @Override
    public List<Admin> findAdmin(String username) {
        AdminExample example = new AdminExample();
        AdminExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        Admin.Column column[] = {Admin.Column.username, Admin.Column.password, Admin.Column.roleIds, Admin.Column.id};
        List<Admin> admins = mapper.selectByExampleSelective(example, column);
        return admins;
    }

    @Override
    public void updateById(Admin admin) {
        mapper.updateByPrimaryKeySelective(admin);
    }
}
