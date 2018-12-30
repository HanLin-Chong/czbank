package com.relesee.domains;

import java.io.Serializable;

/**
 * 此对象用于给前端返回JSON
 */
public class Result<T> implements Serializable {
    private boolean flag;
    private String message;
    private T result;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
