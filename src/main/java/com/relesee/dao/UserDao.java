package com.relesee.dao;

import com.relesee.domains.User;
import org.apache.ibatis.annotations.Param;

public interface UserDao {
    User selectUserById(String userId);

    User selectUserByIdAndPass(@Param("userId") String userId, @Param("pass") String pass);

}
