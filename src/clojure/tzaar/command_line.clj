(ns tzaar.command-line
  (require [tzaar.core :as core]
           [tzaar.util :refer [try-repeatedly]]
           [tzaar.player :refer :all]
           [clojure.string :as string]
           [clojure.edn :as edn]
           [clojure.core.async
            :as a
            :refer [>! <! go go-loop chan put! alts! timeout]]))

(defn- color-to-str [color]
  (string/capitalize (name color)))

(defn command-line-game [white-player black-player board]
  (go-loop [board board
            [player-color & colors] (cycle [:white :black])
            [player & players] (cycle [white-player black-player])]
    (if-not (core/lost? board player-color)
      (let [turn-chan (chan 1)]
        (play
          player
          player-color
          board
          (fn [turn] (put! turn-chan turn)))
        (let [turn (<! turn-chan)]
          (println (str (color-to-str player-color)
                        " played:"
                        \newline
                        (string/join turn \newline)))
          (recur (core/apply-turn board turn)
                 colors
                 players)))
      (println (color-to-str player-color) " loses"))))

(def command-line-player
  (reify Player
    (-play [_ color board play-turn]
      (println (string/capitalize (name color)) " to play:")
      ; TODO Check if move is valid otherwise retry
      (let [validate-move (fn [board move]
                            (if (core/valid-move? board color move)
                              move
                              (throw (Exception. "Invalid move"))))
            parse-attack (fn [expr]
                           {:move-type :attack
                            :from ()
                            :to ()})
            attack-move (try-repeatedly
                          (print "Attack move => ")
                          (->> (read-line)
                               parse-attack
                               (validate-move board))
                          :on-failure (println "Wrong input, try again"))
            board-after-attack (core/apply-move board attack-move)
            parse-move (fn [expr]
                         {:move-type ()
                          })
            second-move (try-repeatedly
                          (print "Second move => ")
                          (->> (read-line)
                               parse-move
                               (validate-move board-after-attack))
                          :on-failure (println "Wrong input, try again"))]
        (play-turn [attack-move second-move])))))