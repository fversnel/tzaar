package tzaar.java;

import tzaar.logger.Logger;

import static tzaar.java.ClojureLayer.FROM_JAVA;
import static tzaar.java.ClojureLayer.COMMAND_LINE;
import static tzaar.java.ClojureLayer.TO_JAVA;
import static tzaar.java.ClojureLayer.LOGGER;

public class Api {
    private Api() { }

    public static final Logger SYSTEM_OUT_LOGGER = (Logger) LOGGER.deref("system-out-logger");
    public static final Logger NO_OP_LOGGER = (Logger) LOGGER.deref("no-op-logger");

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
        final Object winner = COMMAND_LINE.function("command-line-game")
                .invoke(whitePlayer,
                        blackPlayer,
                        FROM_JAVA.invoke(board),
                        logger);
        return (FinishedGame) TO_JAVA.invoke(FinishedGame.class, winner);
    }
}
