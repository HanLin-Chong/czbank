package com.relesee.dao;

import com.relesee.domains.AmazonUSapplication;

public interface AmazonUSdao {

    int insertApplication(AmazonUSapplication amazonUS);

    int updateStatus(AmazonUSapplication amazonUS);

    AmazonUSapplication selectLocked(AmazonUSapplication amazonUSapplication);

    AmazonUSapplication selectOneApplication();

    int updateAuditor(AmazonUSapplication amazonUSapplication);

    int updateApplication(AmazonUSapplication amazonUSapplication);
}
