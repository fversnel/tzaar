package tzaar.java;

import tzaar.util.logging.Logger;

import static tzaar.java.ClojureLayer.*;

public class Api {
    private Api() { }

    public static final Logger SYSTEM_OUT_LOGGER = (Logger) LOGGING.deref("system-out-logger");
    public static final Logger NO_OP_LOGGER = (Logger) LOGGING.deref("no-op-logger");

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

    public static FinishedGame playGame(final tzaar.player.Player whitePlayer,
                                 final tzaar.player.Player blackPlayer,
                                 final Board board) {
        return playGame(whitePlayer, blackPlayer, board, SYSTEM_OUT_LOGGER);
    }

    public static FinishedGame playGame(final tzaar.player.Player whitePlayer,
                                 final tzaar.player.Player blackPlayer,
                                 final Board board,
                                 final Logger logger) {
        final Object winner = GAME.function("play-game")
                .invoke(whitePlayer,
                        blackPlayer,
                        FROM_JAVA.invoke(board),
                        logger);
        return (FinishedGame) TO_JAVA.invoke(FinishedGame.class, winner);
    }
}
