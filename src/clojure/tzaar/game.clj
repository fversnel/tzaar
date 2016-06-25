(ns tzaar.game
  (require [tzaar.core :as core :refer [color-to-str]]
           [tzaar.player :refer [play]]
           [tzaar.util.logging :as logging]
           [clojure.core.async :refer [>! <! <!! go go-loop
                                       chan put! alts! timeout
                                       close!]])
  (:import (tzaar.util.logging Logger)
           (tzaar.player RandomButLegalAI CommandlinePlayer)))

(def random-board core/random-board)
(def default-board core/default-board)

(def random-but-legal-ai (RandomButLegalAI.))
(def command-line-player (CommandlinePlayer.))

(defn play-game
  [white-player black-player board ^Logger l]
  (let [done-chan (chan 1)
        players {:white white-player
                 :black black-player}]
    (go-loop [game-state {:initial-board board
                          :board board
                          :turns []}]
      (let [player-color (core/whos-turn game-state)
            player (get players player-color)
            board (:board game-state)
            turns (:turns game-state)]
        (logging/writeln l turns)
        (logging/writeln l (core/board-to-str board) \newline)
        (if-not (core/lost? game-state)
          (let [turn-chan (chan 1)
                turn-number (inc (count turns))
                play-turn #(do (put! turn-chan %)
                               (close! turn-chan))]
            (play player game-state play-turn)
            (let [turn (<! turn-chan)]
              (logging/writeln l
                               "Turn" (str turn-number ":")
                               (color-to-str player-color)
                               "plays"
                               (core/turn-to-str turn))
              (recur (assoc game-state
                       :board (core/apply-turn board turn)
                       :turns (conj turns turn)))))
          (let [winner (core/opponent-color player-color)]
            (logging/writeln l
                             (color-to-str winner)
                             "wins after" (count turns) "turns")
            (>! done-chan {:initial-board (:initial-board game-state)
                           :turns turns
                           :winner winner})))))
    (<!! done-chan)))