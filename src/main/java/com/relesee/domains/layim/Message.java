package com.relesee.domains.layim;

import java.util.Date;

public class Message {
    private String username;//消息来源用户名
    private String avatar;//消息来源用户头像
    private String id;//消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id）
    private String type;//聊天窗口来源类型，从发送消息传递的to里面获取
    private String content;//消息内容
    private String cid;//消息id，可不传。除非你要对消息进行一些操作（如撤回）
    private boolean mine;//是否我发送的消息，如果为true，则会显示在右方，一般是false
    private String fromid;//消息的发送者id（比如群组中的某个消息发送者），可用于自动解决浏览器多窗口时的一些问题
    private Date timestamp;//服务端时间戳毫秒数。注意：如果你返回的是标准的 unix 时间戳，记得要 *1000
    private String recipientId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
}