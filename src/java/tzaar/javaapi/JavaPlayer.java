//package tzaar.javaapi;
//
//import clojure.java.api.Clojure;
//import clojure.lang.IFn;
//import com.google.gson.Gson;
//
///**
// * Wraps a typed Java Player implementation such that it
// * can be used in the Clojure implementation of Tzaar.
// */
//public class JavaPlayer implements tzaar.player.Player {
//
//    private static final boolean PARSE_KEYWORDS = true;
//    private static final Gson JSON = Json.SERIALIZER;
//
//    private final Player player;
//    private final IFn clojureToJson;
//    private final IFn jsonToClojure;
//
//    public JavaPlayer(Player javaPlayer) {
//        IFn require = Clojure.var("clojure.core", "require");
//        require.invoke(Clojure.read("cheshire.core"));
//
//        clojureToJson = Clojure.var("cheshire.core", "generate-string");
//        jsonToClojure = Clojure.var("cheshire.core", "parse-string");
//
//        this.player = javaPlayer;
//    }
//
//    @Override
//    public Object _play(final Object color, final Object board, final Object playTurn) {
//        final String colorJson = (String) clojureToJson.invoke(color);
//        final String boardJson = (String) clojureToJson.invoke(board);
//
//        player.play(
//                JSON.fromJson(colorJson, Color.class),
//                JSON.fromJson(boardJson, Board.class),
//                turn -> {
//                    Object t = jsonToClojure.invoke(JSON.toJson(turn), PARSE_KEYWORDS);
//                    ((IFn) playTurn).invoke(t);
//                });
//        return null;
//    }
//
//    @Override
//    public String toString() {
//        return player.toString();
//    }
//}
//
