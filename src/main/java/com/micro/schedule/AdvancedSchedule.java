package com.micro.schedule;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Getter
@Slf4j
public class AdvancedSchedule implements Schedule {
    final private UUID schedulerId;
    final private String cronExp;
    final boolean waitForPreviousExecution;
    final private Consumer<String> function;
    private ZonedDateTime lastInvokedTime;
    private AtomicBoolean lastExecutionStillInProgress = new AtomicBoolean(false);

    public AdvancedSchedule(@NonNull UUID schedulerId, @NonNull String cronExp, boolean waitForPreviousExecution, @NonNull Consumer<String> function) {
        this.schedulerId = schedulerId;
        this.cronExp = cronExp;
        this.waitForPreviousExecution = waitForPreviousExecution;
        this.function = function;
    }

    public void invokeRequest(@NonNull ZonedDateTime invokeRequestedTime) {
        log.info("invoke requested at " + invokeRequestedTime);
        if (waitForPreviousExecution) {
            if (!lastExecutionStillInProgress.get()) {
                lastExecutionStillInProgress.set(true);
                lastInvokedTime = invokeRequestedTime;
                try {
                    function.accept(cronExp);
                } catch (Throwable throwable) {
                    log.error(throwable.toString(), throwable);
                }
                lastExecutionStillInProgress.set(false);
            } else {
                log.info("ignoring this invocation");
            }
        } else {
            lastInvokedTime = invokeRequestedTime;
            try {
                function.accept(cronExp);
            } catch (Throwable throwable) {
                log.error(throwable.toString(), throwable);
            }
        }
    }
}
