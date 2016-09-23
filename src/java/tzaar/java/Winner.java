package tzaar.java;

public class Winner {
    public final Color winner;
    public final WinCondition winCondition;

    public Winner(Color winner, WinCondition winCondition) {
        this.winner = winner;
        this.winCondition = winCondition;
    }
}
