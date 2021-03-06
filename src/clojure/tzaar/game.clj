(ns tzaar.game
  (:require [tzaar.core :as core]
            [tzaar.stats :as stats]
            [tzaar.player :refer [play]]
            [tzaar.util.logging :as logging]
            [tzaar.players.rabbitmq :as rabbitmq]
            [tzaar.util.timer :as timer]
            [tzaar.players.ai.provided :as provided-ai]
            [tzaar.players.commandline :refer [->CommandlinePlayer]]
            [clojure.core.async :refer [>! <! <!! go go-loop
                                        chan put! alts! timeout
                                        promise-chan]])
  (:import (tzaar.util.logging Logger)
           (java.util UUID)))

(def random-board core/random-board)
(def default-board core/default-board)

(def random-but-legal-ai (provided-ai/->RandomButLegalAI))
(def attack-then-stack-ai (provided-ai/->AttackThenStackAI))
(def command-line-player (->CommandlinePlayer))
(defn rabbitmq-player [] (rabbitmq/rabbitmq-player))

(defn play-game
  [white-player black-player board ^Logger l]
  (let [done-chan (promise-chan)
        players {:white white-player
                 :black black-player}]
    (go-loop [game-state {:game-id       (UUID/randomUUID)
                          :initial-board board
                          :board         board
                          :turns         []}]
      (let [player-color (core/whos-turn game-state)
            board (:board game-state)
            turns (:turns game-state)
            end-state (core/game-over? game-state)]
        (logging/writeln l (core/board->str board) \newline)
        (if-not end-state
          (let [turn-chan (promise-chan)
                turn-number (inc (count turns))
                player (player-color players)
                play-turn #(put! turn-chan %)]
            (go (play player game-state play-turn))
            (let [turn (<! turn-chan)
                  time-taken (:tzaar/time-taken (meta turn))]
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
                           :turns (conj turns turn))))))
          (do
            (logging/writeln l
                             (core/color->str (:winner end-state))
                             "wins after" (count turns) "turns")
            (>! done-chan {:initial-board (:initial-board game-state)
                           :turns turns
                           :winner (:winner end-state)
                           :win-condition (:win-condition end-state)
                           :stats (stats/stats game-state)})))))
    (<!! done-chan)))