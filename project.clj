(defproject org.fversnel/tzaar "0.1.2-SNAPSHOT"
  :description "Clojure implementation of the abstract strategy game Tzaar by Kris Burm"
  :url "https://github.com/fversnel/tzaar"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [;; Clojure
                 [org.clojure/clojure "1.9.0-alpha12"]
                 [org.clojure/core.async "0.2.385"]
                 [org.clojure/java.data "0.1.1"]
                 [cheshire "5.6.3"]
                 [camel-snake-kebab "0.4.0"]

                 ; Web server stuff
                 [jarohen/chord "0.7.0" :exclusions [org.clojure/clojure
                                                     org.clojure/core.async
                                                     cheshire]]

                 ;; Clojurescript
                 [org.clojure/clojurescript "1.9.229"]]
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[com.taoensso/tufte "1.0.2"]]}}
  :plugins [[lein-cljsbuild "1.1.4"]]
  :source-paths ["src/shared" "src/clojure"]
  :java-source-paths ["src/java"]
  :aot [tzaar.javaapi tzaar.player tzaar.game tzaar.util.logging
        tzaar.players.commandline tzaar.players.ai.provided
        tzaar.util.timer]
  :javac-options ["-target" "1.8" "-source" "1.8"]
  :prep-tasks [["compile" "tzaar.player" "tzaar.util.logging"
                          "tzaar.players.ai.frank2" "tzaar.players.ai.provided"
                          "tzaar.players.commandline"]
               "javac" "compile"]
  :main tzaar.runner

  :cljsbuild {:builds [{:source-paths ["src/shared" "src/clojurescript"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :none
                                   :pretty-print true}}]})