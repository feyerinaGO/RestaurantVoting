package ru.develop.restaurantvoting.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class TimeProvider {
    public static final LocalTime VOTE_DEADLINE = LocalTime.of(11, 0);

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    public boolean canChangeVote(LocalDate voteDate) {
        LocalDateTime now = getCurrentDateTime();
        return voteDate.equals(now.toLocalDate()) &&
                now.toLocalTime().isBefore(VOTE_DEADLINE);
    }
}