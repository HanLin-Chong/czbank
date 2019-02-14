package com.relesee.dao;

import com.relesee.domains.AmazonEUapplication;

public interface AmazonEUdao {

    int insertApplication(AmazonEUapplication amazonEU);

    int updateStatus(AmazonEUapplication amazonEU);

    AmazonEUapplication selectLocked(AmazonEUapplication amazonEUapplication);

    AmazonEUapplication selectOneApplication();

    int updateAuditor(AmazonEUapplication amazonEUapplication);
}
