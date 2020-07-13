package com.chunyue.auth_control_demo.util;

import org.apache.shiro.authz.annotation.RequiresPermissions;

public class Permission {
    private RequiresPermissions requiresPermissions;
    private String api;

    public RequiresPermissions getRequiresPermissions() {
        return requiresPermissions;
    }


    public void setRequiresPermissions(RequiresPermissions requiresPermissions) {
        this.requiresPermissions = requiresPermissions;
    }


    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
