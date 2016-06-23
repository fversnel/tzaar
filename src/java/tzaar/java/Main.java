package tzaar.java;

public class Main {

    public static void main(String[] args) {
        final boolean isStackTypeMissing = Api.defaultBoard().isStackTypeMissing(Color.Black);
        System.out.println(isStackTypeMissing);

        System.out.println(Api.defaultBoard().moves(new Position(0, 0)));
        System.out.println(Api.defaultBoard().attackMoves(new Position(0, 0)));
        System.out.println(Api.defaultBoard().stackMoves(new Position(0, 0)));


        FinishedGame finishedGame = Api.playGame(
                new ExamplePlayer().toClojure(),
                Api.RANDOM_BUT_LEGAL_AI,
                Api.defaultBoard(),
                Api.SYSTEM_OUT_LOGGER);
        System.out.println(finishedGame.winner + " wins");
    }
}
