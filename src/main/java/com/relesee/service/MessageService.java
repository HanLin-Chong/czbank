package com.relesee.service;

import com.relesee.dao.MessageDao;
import com.relesee.dao.UserDao;
import com.relesee.domains.User;
import com.relesee.domains.layim.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Autowired
    MessageDao messageDao;

    public Init init(){
        Init init = new Init();
        List<Friend> friendList = new ArrayList();
        List<Group> groupList = new ArrayList();

        Data data = new Data();

        //我的信息
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Mine mine = new Mine();
        mine.setId(user.getUserId());
        mine.setStatus("online");
        mine.setAvatar(user.getHeadPhoto());
        mine.setSign(user.getSign());
        mine.setUsername(user.getUserName());

        //好友列表，分三组（经理、审核员、系统（root））
        List<User> managerList = userDao.selectAllManager();
        List<User> auditorList = userDao.selectAllAuditor();
        List<User> rootList = userDao.selectAllRoot();

        //好友分组
        Friend managerGroup = new Friend();
        managerGroup.setGroupname("经理");
        managerGroup.setId("0");
        List<Mine> managerMineList = new ArrayList();
        for (User u:managerList){
            Mine m = new Mine();
            m.setUsername(u.getUserName());
            m.setSign(u.getSign());
            m.setAvatar(u.getHeadPhoto());
            m.setStatus("online");
            m.setId(u.getUserId());
            managerMineList.add(m);
        }
        managerGroup.setList(managerMineList);

        Friend auditorGroup = new Friend();
        auditorGroup.setGroupname("审核员");
        auditorGroup.setId("1");
        List<Mine> auditorMineList = new ArrayList();
        for (User u:auditorList){
            Mine m = new Mine();
            m.setUsername(u.getUserName());
            m.setSign(u.getSign());
            m.setAvatar(u.getHeadPhoto());
            m.setStatus("online");
            m.setId(u.getUserId());
            auditorMineList.add(m);
        }
        auditorGroup.setList(auditorMineList);

        Friend rootGroup = new Friend();
        rootGroup.setGroupname("系统");
        rootGroup.setId("2");
        List<Mine> rootMineList = new ArrayList();
        for (User u:rootList){
            Mine m = new Mine();
            m.setUsername(u.getUserName());
            m.setSign(u.getSign());
            m.setAvatar(u.getHeadPhoto());
            m.setStatus("online");
            m.setId(u.getUserId());
            rootMineList.add(m);
        }
        rootGroup.setList(rootMineList);

        friendList.add(managerGroup);
        friendList.add(auditorGroup);
        friendList.add(rootGroup);

        Group allMembers = new Group();
        allMembers.setId("0");
        allMembers.setGroupname("所有人");
        allMembers.setAvatar("default");

        groupList.add(allMembers);

        data.setMine(mine);
        data.setFriend(friendList);
        data.setGroup(groupList);

        init.setCode(0);
        init.setMsg("初始化IM成功");
        init.setData(data);

        return init;
    }

    /**
     * 根据group的Id获取group的成员
     * @param id
     * @return
     */
    public Members members(String id){
        Members members = new Members();
        MemberData data = new MemberData();
        List<Mine> memberList = new ArrayList();
        List<User> userList = messageDao.selectGroupMembers(id);
        members.setCode(0);
        members.setMsg("");
        for (User u:userList){
            Mine mine = new Mine();
            mine.setId(u.getUserId());
            mine.setStatus("online");
            mine.setAvatar("default");
            mine.setSign("");
            mine.setUsername(u.getUserName());
            memberList.add(mine);
        }
        data.setList(memberList);
        members.setData(data);
        return members;
    }
}
