package ru.develop.restaurantvoting;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.develop.restaurantvoting.common.util.TimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@TestConfiguration
public class TestTimeProviderConfig {

    @Bean
    @Primary
    public TestTimeProvider testTimeProvider() {
        return new TestTimeProvider();
    }

    public static class TestTimeProvider extends TimeProvider {
        private LocalTime fixedTime = LocalTime.now();

        public void setCurrentTime(LocalTime time) {
            this.fixedTime = time;
        }

        @Override
        public LocalDateTime getCurrentDateTime() {
            return LocalDateTime.of(LocalDate.now(), fixedTime);
        }

        @Override
        public boolean canChangeVote(LocalDate voteDate) {
            LocalDateTime now = getCurrentDateTime();
            return voteDate.equals(now.toLocalDate()) &&
                    now.toLocalTime().isBefore(VOTE_DEADLINE);
        }

        public void setTimeBeforeDeadline() {
            this.fixedTime = VOTE_DEADLINE.minusMinutes(1);
        }

        public void setTimeAfterDeadline() {
            this.fixedTime = VOTE_DEADLINE.plusMinutes(1);
        }

        public void setTimeOneHourBeforeDeadline() {
            this.fixedTime = VOTE_DEADLINE.minusHours(1);
        }

        public void setTimeThirtyMinutesBeforeDeadline() {
            this.fixedTime = VOTE_DEADLINE.minusMinutes(30);
        }

        public void setTimeAtDeadline() {
            this.fixedTime = VOTE_DEADLINE;
        }
    }
}