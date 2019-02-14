package com.relesee.dao;

import com.relesee.domains.AmazonUSapplication;

public interface AmazonUSdao {

    int insertApplication(AmazonUSapplication amazonUS);

    int updateStatus(AmazonUSapplication amazonUS);
}
