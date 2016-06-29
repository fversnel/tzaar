(ns tzaar.game
  (require [tzaar.core :as core]
           [tzaar.player :refer [play]]
           [tzaar.util.logging :as logging]
           [tzaar.util.timer :as timer]
           [tzaar.players.ai.provided :as provided-ai]
           [tzaar.players.commandline :refer [->CommandlinePlayer]]
           [clojure.core.async :refer [>! <! <!! go go-loop
                                       chan put! alts! timeout
                                       close!]])
  (:import (tzaar.util.logging Logger)))

(def random-board core/random-board)
(def default-board core/default-board)

(def random-but-legal-ai (provided-ai/->RandomButLegalAI))
(def command-line-player (->CommandlinePlayer))

(defn play-game
  [white-player black-player board ^Logger l]
  (let [done-chan (chan 1)
        players {:white white-player
                 :black black-player}]
    (go-loop [game-state {:initial-board board
                          :board board
                          :turns []
                          :stats {:white {:time-taken 0 :total-turns 0}
                                  :black {:time-taken 0 :total-turns 0}}}]
      (let [player-color (core/whos-turn game-state)
            player (player-color players)
            board (:board game-state)
            turns (:turns game-state)]
        (logging/writeln l (core/board->str board) \newline)
        (if-not (core/lost? game-state)
          (let [turn-chan (chan 1)
                turn-number (inc (count turns))
                play-turn #(do (put! turn-chan %)
                               (close! turn-chan))]
            (play player game-state play-turn)
            (let [[turn time-taken] (<! turn-chan)]
              (logging/writeln l
                               "Turn" (str turn-number ":")
                               (core/color->str player-color)
                               "plays"
                               (core/turn->str turn)
                               "in"
                               (timer/format-nanos time-taken))
              (recur
                (-> game-state
                    (assoc :board (core/apply-turn board turn)
                           :turns (conj turns turn))
                    (update-in [:stats player-color :time-taken]
                               (partial + time-taken))
                    (update-in [:stats player-color :total-turns]
                               inc)))))
          (let [winner (core/opponent-color player-color)]
            (logging/writeln l
                             (core/color->str winner)
                             "wins after" (count turns) "turns")
            (>! done-chan {:initial-board (:initial-board game-state)
                           :turns turns
                           :winner winner
                           :stats (:stats game-state)})))))
    (<!! done-chan)))