package tzaar.java;

public class Api {
    private Api() { }

    public static Board defaultBoard() {
        return Board.standard();
    }

    public static Board randomBoard() {
        return Board.random();
    }

    public static final tzaar.player.Player COMMAND_LINE_PLAYER =
            (tzaar.player.Player) ClojureLayer.JAVA_API.deref("command-line-player");
    public static final tzaar.player.Player RANDOM_BUT_LEGAL_AI =
            (tzaar.player.Player) ClojureLayer.JAVA_API.deref("random-but-legal-ai");

    public static Color playGame(final tzaar.player.Player whitePlayer,
                                 final tzaar.player.Player blackPlayer,
                                 final Board board) {
        final Object winner = ClojureLayer.COMMAND_LINE.function("command-line-game")
                .invoke(whitePlayer,
                        blackPlayer,
                        ClojureLayer.FROM_JAVA.invoke(board));
        return (Color) ClojureLayer.TO_JAVA.invoke(Color.class, winner);
    }
}
