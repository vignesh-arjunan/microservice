package com.micro.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transfer implements ResponseMessage {
    private static final long serialVersionUID = 1L;
    private long fromAccount;
    private long toAccount;
    private double amount;
    private String comment;
}