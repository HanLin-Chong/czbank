package com.relesee.dao;

import com.relesee.domains.EbayApplication;

public interface EbayApplicationDao {

    int insertApplication(EbayApplication ebayApplication);

    EbayApplication selectOneEbayApplication();
}
