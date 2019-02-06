package com.relesee.constant;

/**
 * nra申请优先的状态码
 */
public enum NraPriorityStatus {
    NORMAL(0),//未申请优先，普通文件
    WAITING(1),//申请已经提交
    REFUSED(2),//拒绝插队
    PRIORITY(3);//插队文件

    private NraPriorityStatus(int code){
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}
