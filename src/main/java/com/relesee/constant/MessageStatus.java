package com.relesee.constant;

public enum MessageStatus {
    UNRECEIVED(0),
    RECEIVED(1);

    private MessageStatus(int code){
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}
