package com.relesee.socket;

import com.alibaba.fastjson.JSON;
import com.relesee.domains.User;
import com.relesee.domains.layim.Message;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.*;

@Component
public class SocketHandler implements WebSocketHandler {

    private static final Logger logger = Logger.getLogger(SocketHandler.class);
    private static final Map<String, WebSocketSession> sessions = new Hashtable();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        User user = (User) session.getAttributes().get("user");
        sessions.put(user.getUserId(), session);
        logger.info("用户：（"+user.getUserId()+"）建立Socket连接");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        //中转消息
        String content = webSocketMessage.getPayload().toString();

        Message message = JSON.parseObject(content, Message.class);
        message.setTimestamp(new Date());
        String recipientId = message.getRecipientId();

        TextMessage textMessage = new TextMessage(JSON.toJSONString(message));
        WebSocketSession targetSession = sessions.get(recipientId);
        targetSession.sendMessage(textMessage);
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
