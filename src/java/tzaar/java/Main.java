package tzaar.java;

public class Main {

    public static void main(String[] args) {
        Color winner = Api.playGame(
                Api.RANDOM_BUT_LEGAL_AI,
                Api.RANDOM_BUT_LEGAL_AI,
                Api.defaultBoard(),
                Api.SYSTEM_OUT_LOGGER);
        //System.out.println(winner + " wins");
    }
}
