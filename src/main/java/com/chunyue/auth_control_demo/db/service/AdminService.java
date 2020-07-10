package com.chunyue.auth_control_demo.db.service;

import com.chunyue.auth_control_demo.db.domain.Admin;

import java.util.List;

public interface AdminService {
    List<Admin> findAdmin(String username);
}
