package com.relesee.dao;

import com.relesee.domains.User;
import com.relesee.domains.layim.Group;
import com.relesee.domains.layim.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageDao {

    List<User> selectGroupMembers(String groupId);

    int updateUserSign(@Param("sign") String sign, @Param("userId") String UserId);

    int insertMessage(@Param("message") Message message, @Param("statusCode") int statusCode);

    List<Message> selectUnreceivedMessages(@Param("recipientId") String recipientId, @Param("statusCode") int statusCode);

}
