package com.micro.wss;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

@Slf4j
public class ServerWebSocketConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        log.info("modify Handshake called ....");
        log.info("request.getRequestURI() -> {}", request.getRequestURI());
        log.info("request.getHeaders() -> {}", request.getHeaders());
        sec.getUserProperties().put("Allowed", false);
//        if (!headerValues.isEmpty()) {
//            String authHeader = headerValues.get(0);
//
//            Token tokenTK = new Token();
//            String token = tokenTK.parseHeader(authHeader);
//            log.info("Token in the header is >>>> " + token);
//            try {
//                if (tokenTK.verifyToken(token)) {
//                    sec.getUserProperties().put("Allowed", true);
//                }
//            } catch (ServiceException ex) {
//                log.error("Invalid token ...", ex);
//            }
//        } else if (request.getQueryString() != null) {
//            String queryStr = request.getQueryString();
//
//            Token tokenTK = new Token();
//            String token = tokenTK.parseQueryString(queryStr);
//            log.info("Token in the query string is >>>> " + token);
//            try {
//                if (tokenTK.verifyToken(token)) {
//                    sec.getUserProperties().put("Allowed", true);
//                }
//            } catch (ServiceException ex) {
//                log.error("Invalid token ...", ex);
//            }
//        } else {
//            log.error("header and query string missing ...");
//        }
        super.modifyHandshake(sec, request, response);
    }
}
