package core.util;


import java.time.Duration;
import java.time.Instant;

/**
 * Execute a task, and display its duration on stdout.
 */
public class TimeUtils {

    @FunctionalInterface
    public interface TimedTask {
        void execute();
    }

    public static void runWithTime(TimedTask task) {
        Instant start = Instant.now();
        task.execute();
        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }

}
