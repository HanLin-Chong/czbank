package com.relesee.domains.layim;

public class Members {
    private int code;//0表示成功，其它失败
    private String msg;//失败信息
    private MemberData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MemberData getData() {
        return data;
    }

    public void setData(MemberData data) {
        this.data = data;
    }
}
