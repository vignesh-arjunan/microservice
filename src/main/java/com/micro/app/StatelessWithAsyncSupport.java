package com.micro.app;

import lombok.extern.slf4j.Slf4j;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

@Stateless
@Slf4j
public class StatelessWithAsyncSupport {

    @Asynchronous
    public void doSomethingAsynchronous() {
        log.info("doing Asynchronous");
    }
}
