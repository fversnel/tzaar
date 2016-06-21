package tzaar.java;

import clojure.lang.IFn;
import tzaar.java.util.ClojureNamespace;

public class ClojureLayer {
    private ClojureLayer() {}

    public static final ClojureNamespace JAVA_API = new ClojureNamespace("tzaar.javaapi");
    public static final ClojureNamespace GAME = new ClojureNamespace("tzaar.game");
    public static final ClojureNamespace LOGGING = new ClojureNamespace("tzaar.util.logging");

    private static final ClojureNamespace JAVA_CONVERSION = new ClojureNamespace("clojure.java.data");
    public static final IFn FROM_JAVA = JAVA_CONVERSION.function("from-java");
    public static final IFn TO_JAVA = JAVA_CONVERSION.function("to-java");
}
