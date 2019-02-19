package com.relesee.dao;

import com.relesee.domains.EbayApplication;

import java.util.List;

public interface EbayApplicationDao {

    int insertApplication(EbayApplication ebayApplication);

    EbayApplication selectLocked(EbayApplication ebayApplication);

    EbayApplication selectOneEbayApplication();

    int updateStatus(EbayApplication ebayApplication);

    int updateAuditor(EbayApplication ebayApplication);

    int updateApplication(EbayApplication ebayApplication);

    List<EbayApplication> selectMachedFeedback(String accName);
}
