package com.relesee.dao;

import com.relesee.domains.NraFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NraFileDao {
    int insertNraFile(NraFile nraFile);

//    List<NraFile> selectNraQueueLimit(@Param("begin") int begin, @Param("size") int size);

    List<NraFile> selectNraQueue(@Param("fileName") String fileName,@Param("beginDate") String beginDate,@Param("endDate") String endDate);

}
