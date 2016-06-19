package tzaar.java;

public class Api {
    private Api() { }

    public static final Board DEFAULT_BOARD = Board.standard();

    public static Board randomBoard() {
        return Board.random();
    }

    public static final tzaar.player.Player COMMAND_LINE_PLAYER =
            (tzaar.player.Player) ClojureLayer.JAVA_API.deref("command-line-player");
    public static final tzaar.player.Player RANDOM_BUT_LEGAL_PLAYER =
            (tzaar.player.Player) ClojureLayer.JAVA_API.deref("random-but-legal-ai");

    public static void playGame(tzaar.player.Player whitePlayer,
                                tzaar.player.Player blackPlayer,
                                Board board) {
        ClojureLayer.COMMAND_LINE.function("command-line-game")
                .invoke(whitePlayer, blackPlayer, board);
    }
}
