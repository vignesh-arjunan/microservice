package com.micro.schedule;

import lombok.NonNull;

import java.time.ZonedDateTime;

public interface Schedule {
    void invokeRequest(@NonNull ZonedDateTime invokeRequestedTime);
}
