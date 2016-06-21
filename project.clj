(defproject tzaar "0.1.0-SNAPSHOT"
  :description "Clojure implementation of the abstract strategy game Tzaar by Kris Burm"
  :url "https://github.com/fversnel/tzaar"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha7"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/core.async "0.2.385"]
                 [org.clojure/java.data "0.1.1"]
                 [camel-snake-kebab "0.4.0"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :aot [tzaar.javaapi tzaar.player tzaar.command-line]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:unchecked"]
  :prep-tasks [["compile" "tzaar.player"]
               "javac" "compile"]
  :profiles {:uberjar {:aot :all}}
  :main tzaar.java.Main)
