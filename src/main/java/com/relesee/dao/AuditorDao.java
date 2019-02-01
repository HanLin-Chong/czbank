package com.relesee.dao;

import org.apache.ibatis.annotations.Param;

public interface AuditorDao {

    int updateHeadPhoto(@Param("photoName") String photoName, @Param("userId") String userId);

    int aOperation();

    int bOperation();
}
