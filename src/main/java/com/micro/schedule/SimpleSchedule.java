package com.micro.schedule;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Getter
@Slf4j
public class SimpleSchedule {
    final private ZonedDateTime createdTime = ZonedDateTime.now();
    final private UUID schedulerId;
    final private int delay;
    final private TimeUnit timeUnit;
    final boolean waitForPreviousExecution;
    final private Consumer<String> function;
    final private Optional<Integer> repeatCount;
    final private Optional<ZonedDateTime> startTime;
    final private Optional<ZonedDateTime> endTime;
    private ZonedDateTime lastInvokedTime;
    private AtomicBoolean lastExecutionStillInProgress = new AtomicBoolean(false);

    public SimpleSchedule(@NonNull UUID schedulerId, @NonNull int delay, @NonNull TimeUnit timeUnit, @NonNull boolean waitForPreviousExecution, @NonNull Consumer<String> function,
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

    public boolean canInvoke(@NonNull ZonedDateTime invokeRequestedTime) {
        ZonedDateTime delayedTime = null;
        if (timeUnit.equals(TimeUnit.SECONDS)) {
            delayedTime = createdTime.plusSeconds(delay);
        } else if (timeUnit.equals(TimeUnit.MINUTES)) {
            delayedTime = createdTime.plusMinutes(delay);
        } else if (timeUnit.equals(TimeUnit.HOURS)) {
            delayedTime = createdTime.plusHours(delay);
        }
        if (delayedTime.truncatedTo(ChronoUnit.SECONDS).equals(invokeRequestedTime.truncatedTo(ChronoUnit.SECONDS))) {
            return true;
        }
        return false;
    }

    public void invokeRequest(@NonNull ZonedDateTime invokeRequestedTime) {
        log.info("invoke requested at " + invokeRequestedTime);
        if (waitForPreviousExecution && !lastExecutionStillInProgress.get()) {
            lastExecutionStillInProgress.set(true);
            lastInvokedTime = invokeRequestedTime;
            try {
                function.accept(schedulerId.toString());
            } catch (Throwable throwable) {
                log.error(throwable.toString(), throwable);
            }
            lastExecutionStillInProgress.set(false);
        }
    }
}
