package tzaar.java;

import java.time.Duration;

public class Stats {
    public final Duration timeTaken;
    public final int totalTurns;

    public Stats(Duration timeTaken, int totalTurns) {
        this.timeTaken = timeTaken;
        this.totalTurns = totalTurns;
    }

    @Override
    public String toString() {
        return String.format("[Time taken: %s, turns: %d]", formatNanos(timeTaken), totalTurns);
    }

    public static String formatNanos(Duration duration) {
        return (String) ClojureLayer.TIMER.function("format-nanos")
                .invoke(duration.toNanos());
    }
}
