package com.micro.wsc;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class WebsocketClient {

    private WebSocket webSocket;

    @PostConstruct
    public void init() {
        System.out.println("init ");
        WebConnector webConnector = new WebConnector();
        webSocket = webConnector.getWebSocket();
    }

    public void sendText(String msg) {
        CompletableFuture<WebSocket> hello_world = webSocket.sendText(msg, true);
    }
}
