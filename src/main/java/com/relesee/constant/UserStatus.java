package com.relesee.constant;

/**
 * 用户账号状态
 * 停用 = 0
 * 正常 = 1
 * root用户 = 2
 */
public enum UserStatus {

    BLOCK_UP(0),
    NORMAL(1),
    ROOT(2);
    private UserStatus(int code){
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}
