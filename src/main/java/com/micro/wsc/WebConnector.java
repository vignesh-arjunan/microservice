package com.micro.wsc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

public class WebConnector {

    private final CompletableFuture<WebSocket> server_cf;
    private final WebSocket server;
    private final WebSocket.Listener client;

    public WebConnector() {
        client = new WebsocketClientTest();
//        SSLContext context;
//        try {
//            context = SSLContext.getInstance("TLSv1.3");
//            context.init(null, null, null);
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
        String ENDPOINT = "ws://localhost:9091/ws";
        server_cf = HttpClient.newBuilder()/*.sslContext(context)*/.build().newWebSocketBuilder()
                .header("token", "mytoken")
                .buildAsync(URI.create(ENDPOINT), client);
        server = server_cf.join();
    }

    public WebSocket getWebSocket() {
        return this.server;
    }
}
