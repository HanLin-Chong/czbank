package com.relesee.controller;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.layim.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

//配合layim使用，即layim的所有操作都在这
@Controller
public class MessageController {

    @RequiresPermissions( {"auditorController"} )
    @RequestMapping(value="auditor/message/init", produces = "text/plane;charset=utf-8")
    @ResponseBody
    public String init(){
        Init init = new Init();
        List<Friend> friendList = new ArrayList();
        List<Group> groupList = new ArrayList();

        List<Mine> friend1 = new ArrayList();

        Data data = new Data();
        //我的信息
        Mine mine = new Mine();
        mine.setId("root");
        mine.setStatus("online");
        mine.setAvatar("default");
        mine.setSign("编辑你的个性签名");
        mine.setUsername("HanLin");

        Mine mine2 = new Mine();
        mine2.setId("m1");
        mine2.setStatus("online");
        mine2.setAvatar("default");
        mine2.setSign("编辑你的个性签名");
        mine2.setUsername("经理(开发用账号)");

        //好友分组
        Friend friend = new Friend();
        friend.setGroupname("所有人");
        friend.setId("0");
        friend1.add(mine);
        friend.setList(friend1);
        friendList.add(friend);

        //群组
        Group group = new Group();
        group.setId("1");
        group.setGroupname("所有人");
        group.setAvatar("null");
        groupList.add(group);


        data.setMine(mine2);
        data.setFriend(friendList);
        data.setGroup(groupList);
        init.setCode(0);
        init.setMsg("--初始化成功！--");
        init.setData(data);
        System.out.println(JSON.toJSONString(init));
        return JSON.toJSONString(init);
    }

}
