package com.relesee.dao;

import com.relesee.domains.User;
import com.relesee.domains.layim.Group;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageDao {

    List<User> selectGroupMembers(String groupId);

    int updateUserSign(@Param("sign") String sign, @Param("userId") String UserId);

}
