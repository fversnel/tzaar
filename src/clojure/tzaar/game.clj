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

(def initial-stats {:time-taken 0
                    :total-turns 0
                    :total-attack-moves 0
                    :total-stack-moves 0
                    :total-pass-moves 0})

(defn turn->move-stats [turn]
  (->> turn
       (map (fn [{:keys [move-type]}]
              (case move-type
                :attack {:total-attack-moves 1}
                :stack {:total-stack-moves 1}
                :pass {:total-pass-moves 1})))
       (apply merge-with +)))

(defn update-stats [current-stats turn time-taken]
  (merge-with +
              current-stats
              (turn->move-stats turn)
              {:time-taken time-taken :total-turns 1}))

(defn play-game
  [white-player black-player board ^Logger l]
  (let [done-chan (chan 1)
        players {:white white-player
                 :black black-player}]
    (go-loop [game-state {:initial-board board
                          :board board
                          :turns []
                          :stats {:white initial-stats
                                  :black initial-stats}}]
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
                    (update-in [:stats player-color]
                               #(update-stats % turn time-taken))))))
          (let [winner (core/opponent-color player-color)]
            (logging/writeln l
                             (core/color->str winner)
                             "wins after" (count turns) "turns")
            (>! done-chan {:initial-board (:initial-board game-state)
                           :turns turns
                           :winner winner
                           :stats (:stats game-state)})))))
    (<!! done-chan)))