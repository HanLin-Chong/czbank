package com.relesee.dao;

import com.relesee.domains.AmazonUSapplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AmazonUSdao {

    int insertApplication(AmazonUSapplication amazonUS);

    int updateStatus(AmazonUSapplication amazonUS);

    AmazonUSapplication selectLocked(AmazonUSapplication amazonUSapplication);

    AmazonUSapplication selectOneApplication();

    int updateAuditor(AmazonUSapplication amazonUSapplication);

    int updateApplication(AmazonUSapplication amazonUSapplication);

    List<AmazonUSapplication> selectMachedFeedback(String accName);

    List<AmazonUSapplication> selectHistory(@Param("begin") int begin, @Param("size") int size);

    List<AmazonUSapplication> selectPageHistory(@Param("begin") int begin, @Param("size") int size);

    int selectCount();

    List<AmazonUSapplication> selectSearchHistory(String key);

    AmazonUSapplication selectById(String uuid);
}
