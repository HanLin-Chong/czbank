package com.relesee.constant;

/**
 * 境外账户申请的状态码
 */
public enum ForeignApplicationStatus {
    WAITING(0),
    LOCKED(1),
    SUBMITTED(2),
    RETREAT(3),
    FEEDBACK(4);

    private ForeignApplicationStatus(int code){
        this.code = code;
    }

    private int code;

    public int getCode(){
        return code;
    }
}
