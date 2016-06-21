package tzaar.java;

public class Main {

    public static void main(String[] args) {
        FinishedGame finishedGame = Api.playGame(
                Api.RANDOM_BUT_LEGAL_AI,
                Api.RANDOM_BUT_LEGAL_AI,
                Api.defaultBoard(),
                Api.SYSTEM_OUT_LOGGER);
        System.out.println(finishedGame.winner + " wins");
    }
}
