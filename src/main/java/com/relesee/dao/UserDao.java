package com.relesee.dao;

import com.relesee.domains.User;
import org.apache.ibatis.annotations.Param;

public interface UserDao {
    User selectUserById(String userId);

    User selectUserByIdAndPass(@Param("userId") String userId, @Param("pass") String pass);

    /**
     * 用于客户经理可自己更改的信息
     * @return
     */
    int updatePersonalInformation(User user);

    int updatePassword(@Param("userId") String userId,@Param("password") String password);

}
