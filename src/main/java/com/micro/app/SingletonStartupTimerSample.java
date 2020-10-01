package com.micro.app;

import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.micro.schedule.AdvancedSchedule;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cronutils.model.CronType.QUARTZ;

@Singleton
@Startup
@Slf4j
public class SingletonStartupTimerSample {

    @Resource
    private ManagedExecutorService mes;
    private final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);
    private final CronParser parser = new CronParser(cronDefinition);
    private final List<AdvancedSchedule> schedules = new ArrayList<>();

    @Inject
    private StatelessWithAsyncSupport stateless;

    @PostConstruct
    public void init() {
        log.info("in init");
//        stateless.doSomethingAsynchronous();
//        mes.submit(() -> log.info("init on executor"));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "0 21 16 ? 10/1 THU#1 *", true,
                this::function));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "0 22 16 ? 10/1 THU#1 *", true,
                this::function));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 35 16 ? 10/1 THU#1 *", true,
                this::function));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "0 16 17 ? 10/1 THU#1 *", true,
                this::function));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "0 17 17 ? 10/1 THU#1 *", true,
                this::function));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 18 17 ? 10/1 THU#1 *", true,
                this::function));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 19 17 ? 10/1 THU#1 *", true,
                this::function));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 20 17 ? 10/1 THU#1 *", true,
                this::function));
        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 23 17 ? 10/1 THU#1 *", true,
                this::function));
    }

    private String function(String input) {
        log.info("calling function with Input " + input);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return input;
    }

    @PreDestroy
    public void destroy() {
        log.info("in destroy");
    }

    @javax.ejb.Schedule(second = "*/1", minute = "*", hour = "*", persistent = false)
    public void automaticTimeout() {
        final ZonedDateTime now = ZonedDateTime.now();
        //log.info("Automatic timeout occurred at " + now);
        mes.execute(() -> {
                    schedules.forEach(schedule -> {
                        //log.info("processing for schedule " + schedule.getSchedulerId());
                        ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(schedule.getCronExp()));
                        //log.info("executionTime.isMatch(now) = " + executionTime.isMatch(now));
                        if (executionTime.isMatch(now)) {
                            mes.execute(() -> schedule.invokeRequest(now));
                        }
                    });
                }
        );
    }
}
