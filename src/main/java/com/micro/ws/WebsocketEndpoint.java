package com.micro.ws;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
@ServerEndpoint("/ws")
@Slf4j
public class WebsocketEndpoint {

    private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("in init");
    }

    @OnMessage
    public void processGreeting(String message, Session session) {
        log.info("Greeting received: " + message);
    }

    @OnOpen
    public void open(Session session) {
        sessionMap.put(session.getId(), session);
        log.info("sessionMap.size() {}",sessionMap.size());
        log.info("New session opened: " + session.getId());
    }

    @OnError
    public void error(Session session, Throwable t) {
        sessionMap.remove(session.getId());
        log.info("sessionMap.size() {}",sessionMap.size());
        log.info("Error on session " + session.getId());
    }

    @OnClose
    public void closedConnection(Session session) {
        sessionMap.remove(session.getId());
        log.info("sessionMap.size() {}",sessionMap.size());
        log.info("session closed: " + session.getId());
    }
}
