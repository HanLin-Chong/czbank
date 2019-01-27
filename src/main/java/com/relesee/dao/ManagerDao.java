package com.relesee.dao;

import org.apache.ibatis.annotations.Param;

public interface ManagerDao {

    int insertAdvice(@Param("content") String content, @Param("managerId") String managerId);

    int updateHeadPhoto(@Param("photoName") String photoName, @Param("userId") String userId);
}
