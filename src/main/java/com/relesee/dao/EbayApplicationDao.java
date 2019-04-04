package com.relesee.dao;

import com.relesee.domains.EbayApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EbayApplicationDao {

    int insertApplication(EbayApplication ebayApplication);

    EbayApplication selectLocked(EbayApplication ebayApplication);

    EbayApplication selectOneEbayApplication();

    int updateStatus(EbayApplication ebayApplication);

    int updateAuditor(EbayApplication ebayApplication);

    int updateApplication(EbayApplication ebayApplication);

    List<EbayApplication> selectMachedFeedback(String accName);

    List<EbayApplication> selectHistory(@Param("begin") int begin, @Param("size") int size);

    List<EbayApplication> selectPageHistory(@Param("begin") int begin, @Param("size") int size);

    int selectCount();

    List<EbayApplication> selectSearchHistory(String key);

    EbayApplication selectById(String uuid);
}
