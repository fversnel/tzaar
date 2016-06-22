(ns tzaar.game
  (require [tzaar.core :as core :refer [color-to-str]]
           [tzaar.player :refer [play]]
           [tzaar.util.logging :as logger]
           [clojure.core.async :refer [>! <! <!! go go-loop
                                       chan put! alts! timeout]])
  (:import (tzaar.util.logging Logger)))

(defn- flip-color [color]
  (if (= color :white) :black :white))

(defn play-game
  [white-player black-player board ^Logger l]
  (let [done-chan (chan 1)
        initial-board board]
    (go-loop [board board
              [player-color & colors] (cycle [:white :black])
              [player & players] (cycle [white-player black-player])
              turns []]
      (logger/writeln l (core/board-to-str board) \newline)
      (if-not (core/lost? board player-color true)
        (let [turn-chan (chan 1)
              first-turn? (empty? turns)
              turn-number (inc (count turns))
              play-turn #(put! turn-chan %)]
          (play player player-color board first-turn? play-turn)
          (let [turn (<! turn-chan)]
            (logger/writeln l
                            "Turn" (str turn-number ":")
                            (color-to-str player-color)
                            "plays"
                            (core/turn-to-str turn))
            (recur (core/apply-turn board turn)
                   colors
                   players
                   (conj turns turn))))
        (let [winner (flip-color player-color)]
          (logger/writeln l
                          (color-to-str winner)
                          "wins after" (count turns) "turns")
          (>! done-chan {:initial-board initial-board
                         :turns turns
                         :winner winner}))))
    (<!! done-chan)))

