package com.chunyue.auth_control_demo.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/test")
public class TestAuthController {

    @RequiresPermissions("test1")
    @GetMapping("/1")
    public String test1(){
        return "success";
    }

    @RequiresPermissions("test2")
    @GetMapping("/2")
    public String test2(){
        return "success";
    }
}
