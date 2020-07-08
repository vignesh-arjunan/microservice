package com.micro.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;

@RequestScoped
@Getter
@Setter
public class ReqScopedBean {
    private String comment;
}
