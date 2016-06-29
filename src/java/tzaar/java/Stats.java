package tzaar.java;

import java.time.Duration;

public class Stats {
    public final Duration timeTaken;
    public final int totalTurns;
    public final int totalAttackMoves;
    public final int totalStackMoves;
    public final int totalPassMoves;

    public Stats(final Duration timeTaken, final int totalTurns,
                 final int totalAttackMoves, final int totalStackMoves,
                 final int totalPassMoves) {
        this.timeTaken = timeTaken;
        this.totalTurns = totalTurns;
        this.totalAttackMoves = totalAttackMoves;
        this.totalStackMoves = totalStackMoves;
        this.totalPassMoves = totalPassMoves;
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
