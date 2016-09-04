(defproject org.fversnel/tzaar "0.1.1-SNAPSHOT"
  :description "Clojure implementation of the abstract strategy game Tzaar by Kris Burm"
  :url "https://github.com/fversnel/tzaar"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha11"]
                 [org.clojure/core.async "0.2.385"]
                 [org.clojure/java.data "0.1.1"]
                 [cheshire "5.6.3"]
                 [camel-snake-kebab "0.4.0"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :aot [tzaar.javaapi tzaar.player tzaar.game tzaar.util.logging
        tzaar.players.commandline tzaar.players.ai.provided
        tzaar.util.timer]
  :javac-options ["-target" "1.8" "-source" "1.8"]
  :prep-tasks [["compile" "tzaar.player" "tzaar.util.logging"
                          "tzaar.players.ai.frank2" "tzaar.players.ai.provided"
                          "tzaar.players.commandline"]
               "javac" "compile"]
  :profiles {:uberjar {:aot :all}}
  :main tzaar.runner)
