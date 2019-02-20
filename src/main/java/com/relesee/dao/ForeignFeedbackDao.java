package com.relesee.dao;

import com.relesee.domains.ForeignFeedback;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ForeignFeedbackDao {
    int insertFeedback(@Param("feedbacks") List<ForeignFeedback> feedbacks);

    ForeignFeedback selectMachedFeedback(String businessName);
}
