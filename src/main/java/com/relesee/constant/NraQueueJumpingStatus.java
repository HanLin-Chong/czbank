package com.relesee.constant;

/**
 * Nra文件插队申请状态
 * 普通文件 = 0
 * 插队申请已提交 = 1
 * 拒绝插队 = 2（这种情况出现太多是不是要提示一下客户经理，限制其申请优先）
 * 插队文件 = 3
 */
public enum NraQueueJumpingStatus {
    NORMAL_FILE(0),
    SUBMITTED(1),
    REFUSED(2),
    PRIORITY_FILE(3);

    private NraQueueJumpingStatus(int code){
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}
