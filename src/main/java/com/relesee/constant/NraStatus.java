package com.relesee.constant;

/**
 * NRA文件队列状态
 * 排队中 = 0
 * 已锁定 = 1
 * 审核通过 = 2
 * 拒绝申请 = 3
 * 主动取消 = 4
 */
public enum NraStatus {
    QUEUING(0),
    LOCKED(1),
    PASS(2),
    REFUSED(3),
    CANCELED(4);

    private int statusCode;

    private NraStatus(int statusCode){
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
