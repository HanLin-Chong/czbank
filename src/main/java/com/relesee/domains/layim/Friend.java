package com.relesee.domains.layim;

import java.util.List;

/**
 * 分组多的时候一般是List<Friend>
 * 好友信息
 */
public class Friend {
    private String groupname;//好友分组名
    private String id;//分组ID
    private List<Mine> list;//当前分组分组下的好友列表

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Mine> getList() {
        return list;
    }

    public void setList(List<Mine> list) {
        this.list = list;
    }
}
