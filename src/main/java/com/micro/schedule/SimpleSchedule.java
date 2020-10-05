package com.micro.schedule;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Getter
@Slf4j
public class SimpleSchedule implements Schedule {
    final private ZonedDateTime createdTime = ZonedDateTime.now();
    final private UUID schedulerId;
    final private Integer delay;
    final private TimeUnit timeUnit;
    final boolean waitForPreviousExecution;
    final private Consumer<String> function;
    final private Optional<Integer> repeatCount;
    final private Optional<ZonedDateTime> startTime;
    final private Optional<ZonedDateTime> endTime;
    private ZonedDateTime lastInvokedTime;
    private AtomicBoolean lastExecutionStillInProgress = new AtomicBoolean(false);
    private int executionCounter = 0;

    public SimpleSchedule(@NonNull UUID schedulerId, @NonNull Integer delay, @NonNull TimeUnit timeUnit, @NonNull boolean waitForPreviousExecution, @NonNull Consumer<String> function,
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
        int delayFactor = 1;
        if (repeatCount.isPresent()) {
            if (executionCounter >= repeatCount.get()) {
                return false;
            }
            delayFactor = executionCounter + 1;
        }

        if (startTime.isPresent() && endTime.isPresent()) {
            ZonedDateTime delayedTime = getDelayedTime(startTime.get(), delayFactor);
            return delayedTime.truncatedTo(ChronoUnit.SECONDS).equals(invokeRequestedTime.truncatedTo(ChronoUnit.SECONDS)) &&
                    delayedTime.truncatedTo(ChronoUnit.SECONDS).isBefore(invokeRequestedTime.truncatedTo(ChronoUnit.SECONDS));
        }

        if (startTime.isPresent()) {
            ZonedDateTime delayedTime = getDelayedTime(startTime.get(), delayFactor);
            return delayedTime.truncatedTo(ChronoUnit.SECONDS).equals(invokeRequestedTime.truncatedTo(ChronoUnit.SECONDS));
        }

        ZonedDateTime delayedTime = getDelayedTime(createdTime, delayFactor);
        if (delayedTime.truncatedTo(ChronoUnit.SECONDS).equals(invokeRequestedTime.truncatedTo(ChronoUnit.SECONDS))) {
            return true;
        }
        return false;
    }

    public void invokeRequest(@NonNull ZonedDateTime invokeRequestedTime) {
        executionCounter++;
        log.info("invoke requested at " + invokeRequestedTime.toLocalDateTime());
        if (waitForPreviousExecution && !lastExecutionStillInProgress.get()) {
            lastExecutionStillInProgress.set(true);
            lastInvokedTime = invokeRequestedTime;
            invokeFunctionSafely();
            lastExecutionStillInProgress.set(false);
        } else {
            lastInvokedTime = invokeRequestedTime;
            invokeFunctionSafely();
        }
    }

    private void invokeFunctionSafely() {
        try {
            function.accept(schedulerId.toString());
        } catch (Throwable throwable) {
            log.error(throwable.toString(), throwable);
        }
    }

    private ZonedDateTime getDelayedTime(ZonedDateTime inputZonedDateTime, int delayFactor) {
        ZonedDateTime delayedTime = null;
        if (timeUnit.equals(TimeUnit.SECONDS)) {
            delayedTime = inputZonedDateTime.plusSeconds(delay * delayFactor);
        } else if (timeUnit.equals(TimeUnit.MINUTES)) {
            delayedTime = inputZonedDateTime.plusMinutes(delay * delayFactor);
        } else if (timeUnit.equals(TimeUnit.HOURS)) {
            delayedTime = inputZonedDateTime.plusHours(delay * delayFactor);
        }
        return delayedTime.withZoneSameInstant(ZoneId.systemDefault());
    }
}
