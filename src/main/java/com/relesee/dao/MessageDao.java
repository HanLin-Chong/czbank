package com.relesee.dao;

import com.relesee.domains.User;
import com.relesee.domains.layim.Group;

import java.util.List;

public interface MessageDao {

    List<User> selectGroupMembers(String groupId);



}
