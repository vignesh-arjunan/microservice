package com.micro.app;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.time.LocalTime;

@Singleton
@Startup
@Slf4j
public class SingletonStartupTimerSample {

    @Resource
    private ManagedExecutorService mes;

    @Inject
    private StatelessWithAsyncSupport stateless;

    @PostConstruct
    public void init() {
        log.info("in init");
        mes.submit(() -> log.info("init on executor"));
    }


    @PreDestroy
    public void destroy() {
        log.info("in destroy");
    }

    @Schedule(minute = "*/1", hour = "*", persistent = false)
    public void automaticTimeout() {
        log.info("Automatic timeout occurred at " + LocalTime.now());
        stateless.doSomethingAsynchronous();
    }
}
