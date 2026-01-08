package ru.develop.restaurantvoting.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class TimeProvider {

    public LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    public boolean isBeforeDeadline() {
        return getCurrentTime().isBefore(LocalTime.of(11, 0));
    }
}