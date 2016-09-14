(ns tzaar.players.ai.provided
  (:require [tzaar.player :refer [Player]]
            [tzaar.rules :refer :all]
            [tzaar.rules :as core]))

(defn- attack-moves [game-state]
  (->> (all-moves game-state)
       (filter attack-move?)))

(defn- stack-moves [game-state]
  (->> (all-moves game-state)
       (filter stack-move?)))

(defn- random-move [moves]
  (when-not (empty? moves)
    (rand-nth moves)))

;;always plays a completely random move
;;it will only pass the second move
;;of a turn if no other moves are available
(defrecord RandomButLegalAI []
  tzaar.player/Player
  (-play [_ game-state play-turn]
    (let [attack-move (->> (attack-moves game-state)
                           rand-nth)
          second-move (let [new-state (apply-move game-state attack-move)]
                        (-> (all-moves new-state)
                            random-move
                            (or pass-move)))
          turn (if (first-turn? game-state)
                 [attack-move]
                 [attack-move second-move])]
      (play-turn turn))))

;;always prefers stacking moves over attack moves
(defrecord AttackThenStackAI []
  tzaar.player/Player
  (-play [_ game-state play-turn]
    (let [attack-move (->> (attack-moves game-state)
                           rand-nth)
          second-move (let [new-state (apply-move game-state attack-move)]
                        (or (random-move (stack-moves new-state))
                            (random-move (attack-moves new-state))
                            pass-move))
          turn (if (first-turn? game-state)
                 [attack-move]
                 [attack-move second-move])]
      (play-turn turn))))

;;the absolute most stupid AI available
;;it always resigns, no matter what.
;;(can be useful for testing though)
(defrecord ResignerAI []
  tzaar.player/Player
  (-play [_ _ play-turn]
    (play-turn core/resignation)))