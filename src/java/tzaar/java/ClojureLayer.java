package tzaar.java;

import clojure.lang.IFn;
import tzaar.java.util.ClojureNamespace;

public class ClojureLayer {
    private ClojureLayer() {}

    public static final ClojureNamespace JAVA_API = new ClojureNamespace("tzaar.javaapi");
    public static final ClojureNamespace COMMAND_LINE = new ClojureNamespace("tzaar.command-line");
    public static final ClojureNamespace LOGGER = new ClojureNamespace("tzaar.logger");

    private static final ClojureNamespace JAVA_CONVERSION = new ClojureNamespace("clojure.java.data");
    public static final IFn FROM_JAVA = JAVA_CONVERSION.function("from-java");
    public static final IFn TO_JAVA = JAVA_CONVERSION.function("to-java");
}
