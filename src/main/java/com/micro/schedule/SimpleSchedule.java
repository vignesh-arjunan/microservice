package com.micro.schedule;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Getter
@Slf4j
public class SimpleSchedule {
    final private UUID schedulerId;
    final private int delay;
    final private TimeUnit timeUnit;
    final boolean waitForPreviousExecution;
    final private Function<String, String> function;
    final private Optional<Integer> repeatCount;
    final private Optional<ZonedDateTime> startTime;
    final private Optional<ZonedDateTime> endTime;
    private ZonedDateTime lastInvokedTime;
    private AtomicBoolean lastExecutionStillInProgress = new AtomicBoolean(false);

    public SimpleSchedule(@NonNull UUID schedulerId, @NonNull int delay, @NonNull TimeUnit timeUnit, @NonNull boolean waitForPreviousExecution, @NonNull Function<String, String> function,
                          Optional<Integer> repeatCount, Optional<ZonedDateTime> startTime, Optional<ZonedDateTime> endTime) {
        this.schedulerId = schedulerId;
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.waitForPreviousExecution = waitForPreviousExecution;
        this.function = function;
        this.repeatCount = repeatCount;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void invokeRequest(ZonedDateTime invokeRequestedTime) {
        log.info("invoke requested at " + invokeRequestedTime);
        if (waitForPreviousExecution && !lastExecutionStillInProgress.get()) {
            lastExecutionStillInProgress.set(true);
            lastInvokedTime = invokeRequestedTime;
//            try {
//                function.apply(cronExp);
//            } catch (Throwable throwable) {
//                log.error(throwable.toString(), throwable);
//            }
            lastExecutionStillInProgress.set(false);
        }
    }
}
