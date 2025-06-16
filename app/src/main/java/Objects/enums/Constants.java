package Objects.enums;

import java.time.LocalTime;

public class Constants {

    public enum ShiftTime {
        START(LocalTime.of(8, 30)),
        END(LocalTime.of(17, 30)); // optional

        private final LocalTime time;

        ShiftTime(LocalTime time) {
            this.time = time;
        }

        public LocalTime getTime() {
            return time;
        }
    }

    // Example: you can add more enums here
    public enum Department {
        HR,
        IT,
        SALES,
        FINANCE
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        ON_LEAVE
    }

    // Add more enums as needed...
}
