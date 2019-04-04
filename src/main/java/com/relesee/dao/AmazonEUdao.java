package com.relesee.dao;

import com.relesee.domains.AmazonEUapplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AmazonEUdao {

    int insertApplication(AmazonEUapplication amazonEU);

    int updateStatus(AmazonEUapplication amazonEU);

    AmazonEUapplication selectLocked(AmazonEUapplication amazonEUapplication);

    AmazonEUapplication selectOneApplication();

    int updateAuditor(AmazonEUapplication amazonEUapplication);

    int updateApplication(AmazonEUapplication amazonEUapplication);

    List<AmazonEUapplication> selectHistory(@Param("begin") int begin, @Param("size") int size);

    int selectCount();

    List<AmazonEUapplication> selectSearchHistory(String key);

    AmazonEUapplication selectById(String uuid);
}