package com.relesee.socket;

import com.alibaba.fastjson.JSON;
import com.relesee.constant.MessageStatus;
import com.relesee.dao.MessageDao;
import com.relesee.domains.User;
import com.relesee.domains.layim.Message;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.*;

@Component
public class SocketHandler implements WebSocketHandler {

    private static final Logger logger = Logger.getLogger(SocketHandler.class);
    private static final Map<String, WebSocketSession> sessions = new Hashtable();

    @Autowired
    MessageDao messageDao;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        User user = (User) session.getAttributes().get("user");
        sessions.put(user.getUserId(), session);
        logger.info("用户：（"+user.getUserId()+"）建立Socket连接");

        //检查离线消息，同时更新状态为已读
        List<Message> unreceived = messageDao.selectUnreceivedMessages(user.getUserId(), MessageStatus.UNRECEIVED.getCode());
        if (unreceived.size() > 0){
            for (Message message:unreceived){
                TextMessage textMessage = new TextMessage(JSON.toJSONString(message));
//                sessions.get(message.getRecipientId()).sendMessage(textMessage);
                session.sendMessage(textMessage);
            }

        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        //中转消息

        String content = webSocketMessage.getPayload().toString();


        try {
            Message message = JSON.parseObject(content, Message.class);
            message.setTimestamp(new Date());
            String recipientId = message.getRecipientId();

            if (sessions.containsKey(recipientId)){
                WebSocketSession targetSession = sessions.get(recipientId);
                if (targetSession.isOpen()){
                    //如果在线
                    int count = messageDao.insertMessage(message, MessageStatus.RECEIVED.getCode());
                    if (count != 1){
                        logger.error("在线消息插入数据库失败");
                    }
                    TextMessage textMessage = new TextMessage(JSON.toJSONString(message));
                    targetSession.sendMessage(textMessage);
                } else {
                    int count = messageDao.insertMessage(message, MessageStatus.UNRECEIVED.getCode());
                    if (count != 1){
                        logger.error("离线消息插入数据库失败");
                    }
                }

            } else {
                int count = messageDao.insertMessage(message, MessageStatus.UNRECEIVED.getCode());
                if (count != 1){
                    logger.error("离线消息插入数据库失败");
                }
            }

        } catch (Exception e){
            e.printStackTrace();
            //logger.info("socket“心跳”："+content);
        }


    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        User user = (User) session.getAttributes().get("user");
        logger.error("用户：（"+user.getUserId()+"）socket出错", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        User user = (User) session.getAttributes().get("user");
        //sessions.remove(user.getUserId());
        logger.info("用户：（"+user.getUserId()+"）socket连接中断,中断码："+closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}
