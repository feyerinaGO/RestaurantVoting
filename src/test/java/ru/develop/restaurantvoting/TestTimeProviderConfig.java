package ru.develop.restaurantvoting;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.develop.restaurantvoting.common.util.TimeProvider;

import java.time.LocalTime;

@TestConfiguration
public class TestTimeProviderConfig {

    @Bean
    @Primary
    public TimeProvider testTimeProvider() {
        return new TestTimeProvider();
    }

    public static class TestTimeProvider extends TimeProvider {
        private LocalTime mockTime;

        public void setMockTime(LocalTime time) {
            this.mockTime = time;
        }

        @Override
        public LocalTime getCurrentTime() {
            return mockTime != null ? mockTime : LocalTime.now();
        }

        @Override
        public boolean isBeforeDeadline() {
            return getCurrentTime().isBefore(LocalTime.of(11, 0));
        }
    }
}