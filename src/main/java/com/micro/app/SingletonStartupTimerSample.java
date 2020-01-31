package com.micro.app;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import java.util.Date;

@Singleton
@Startup
@Slf4j
public class SingletonStartupTimerSample {

    @Resource
    private ManagedExecutorService mes;

    @PostConstruct
    public void init() {
        log.info("in init");
        mes.submit(() -> log.info("init on executor"));
    }

    @PreDestroy
    public void destroy() {
        log.info("in destroy");
        log.info("closing client ...");
    }

    @Schedule(second = "*/1", minute = "*", hour = "*", persistent = false)
    public void automaticTimeout() {
        log.info("Automatic timeout occured");
    }
}
