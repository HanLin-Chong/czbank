package com.relesee.dao;

import com.relesee.domains.NraFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NraFileDao {
    int insertNraFile(NraFile nraFile);

//    List<NraFile> selectNraQueueLimit(@Param("begin") int begin, @Param("size") int size);

    List<NraFile> selectNraQueue(@Param("fileName") String fileName,@Param("beginDate") String beginDate,@Param("endDate") String endDate);

    int deleteNraFileById(String id);

    List<NraFile> selectNraHistory(@Param("userId")String userId, @Param("fileName") String fileName,@Param("beginDate") String beginDate,@Param("endDate") String endDate);

    NraFile selectNraFileById(String Id);

    int updateStatusRefused(NraFile nraFile);

    int updateStatusPassed(NraFile nraFile);

    /**
     * 在锁定资源以后，就要取出锁定的资源，与锁定资源属于同一个事物，由synchronized锁定
     * @param amount
     * @param auditor
     * @return
     */
    List<NraFile> selectForAudit(@Param("amount")int amount, @Param("auditor") String auditor);

    /**
     * 锁定资源，审核员自定义取走队列的前amount个文件
     * @param amount
     * @param auditor
     * @return
     */
    int updateNraAuditor(@Param("amount")int amount, @Param("auditor") String auditor);
}
