package com.relesee.dao;

import java.util.Set;

public interface RolePermissionDao {

    Set<String> selectPermissionByRole(String role);

}
