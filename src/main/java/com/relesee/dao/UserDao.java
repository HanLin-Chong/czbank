package com.relesee.dao;

import com.relesee.domains.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    User selectUserById(String userId);

    User selectUserByIdAndPass(@Param("userId") String userId, @Param("pass") String pass);

    /**
     * 用于客户经理可自己更改的信息
     * @return
     */
    int updatePersonalInformation(User user);

    int updatePassword(@Param("userId") String userId,@Param("password") String password);

    int insertManager(User user);

    int selectCountManagerId(String userId);

    List<User> selectManager(@Param("userName") String userName, @Param("phone") String phone, @Param("email") String email);

    int updateManagerBlock(@Param("userId") String userId, @Param("userState") int userState);

    int updateManagerActive(@Param("userId") String userId, @Param("userState") int userState);

    //一下是消息模块的操作

    List<User> selectAllManager();

    List<User> selectAllAuditor();

    List<User> selectAllRoot();

    int temp(User user);
}
