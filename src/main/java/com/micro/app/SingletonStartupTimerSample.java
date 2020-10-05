package com.micro.app;

import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.micro.schedule.AdvancedSchedule;
import com.micro.schedule.Schedule;
import com.micro.schedule.SimpleSchedule;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.cronutils.model.CronType.QUARTZ;

@Singleton
@Startup
@Slf4j
public class SingletonStartupTimerSample {

    @Resource
    private ManagedExecutorService mes;
    private final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);
    private final CronParser parser = new CronParser(cronDefinition);
    private final List<Schedule> schedules = new ArrayList<>();

    @Inject
    private StatelessWithAsyncSupport stateless;

    @PostConstruct
    public void init() {
        log.info("in init");
//        stateless.doSomethingAsynchronous();
//        mes.submit(() -> log.info("init on executor"));

//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "0 01 20 ? 10/1 THU#1 *", true,
//                this::function));
//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "0 02 20 ? 10/1 THU#1 *", true,
//                this::function));
//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 05 20 ? 10/1 THU#1 *", true,
//                this::function));
//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "0 16 20 ? 10/1 THU#1 *", true,
//                this::function));
//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "0 17 20 ? 10/1 THU#1 *", true,
//                this::function));
//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 18 20 ? 10/1 THU#1 *", true,
//                this::function));
//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 19 20 ? 10/1 THU#1 *", true,
//                this::function));
//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 20 20 ? 10/1 THU#1 *", true,
//                this::function));
//        schedules.add(new AdvancedSchedule(UUID.randomUUID(), "* 23 20 ? 10/1 THU#1 *", true,
//                this::function));

        schedules.add(new SimpleSchedule(UUID.randomUUID(), 10, TimeUnit.SECONDS, false, this::function,
                Optional.of(2), Optional.empty(), Optional.empty()));
        schedules.add(new SimpleSchedule(UUID.randomUUID(), 10, TimeUnit.SECONDS, false, this::function,
                Optional.of(2), Optional.of(ZonedDateTime.parse("2020-10-05T10:58:00.000Z", DateTimeFormatter.ISO_ZONED_DATE_TIME)), Optional.empty()));
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
                        if (schedule instanceof AdvancedSchedule) {
                            AdvancedSchedule advancedSchedule = (AdvancedSchedule) schedule;
                            //log.info("processing for schedule " + schedule.getSchedulerId());
                            ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(advancedSchedule.getCronExp()));
                            //log.info("executionTime.isMatch(now) = " + executionTime.isMatch(now));
                            if (executionTime.isMatch(now)) {
                                mes.execute(() -> schedule.invokeRequest(now));
                            }
                        } else {
                            SimpleSchedule simpleSchedule = (SimpleSchedule) schedule;
                            //log.info("simpleSchedule = " + simpleSchedule);
                            if (simpleSchedule.canInvoke(now)) {
                                mes.execute(() -> schedule.invokeRequest(now));
                            }
                        }
                    });
                }
        );
    }
}
