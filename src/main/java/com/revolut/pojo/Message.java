package com.revolut.pojo;

import lombok.Getter;

@Getter
public class Message {
    private static final long serialVersionUID = 1L;
    private final String msg;

    public Message(String msg) {
        this.msg = msg;
    }
}