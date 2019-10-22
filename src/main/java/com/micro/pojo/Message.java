package com.micro.pojo;

import lombok.Getter;

@Getter
public class Message implements ResponseMessage {
    private static final long serialVersionUID = 1L;
    private final String msg;
    private final Throwable cause;

    public Message(String msg, Throwable cause) {
        this.msg = msg;
        this.cause = cause;
    }
}