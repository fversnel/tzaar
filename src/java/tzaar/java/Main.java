package tzaar.java;

public class Main {

    public static void main(String[] args) {
        Color winner = Api.playGame(
                Api.COMMAND_LINE_PLAYER,
                Api.RANDOM_BUT_LEGAL_AI,
                Api.defaultBoard());
    }
}
