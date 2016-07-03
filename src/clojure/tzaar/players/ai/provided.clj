(ns tzaar.players.ai.provided
  (:require [tzaar.player :refer [Player]]
            [tzaar.core :refer :all]
            [tzaar.core :as core]))

(defn- attack-moves [board color]
  (->> (all-moves board color)
       (filter core/attack-move?)))

(defn- stack-moves [board color]
  (->> (all-moves board color)
       (filter core/stack-move?)))

(defn- random-move [moves]
  (when-not (empty? moves)
    (rand-nth moves)))

(defrecord RandomButLegalAI []
  tzaar.player/Player
  (-play [_ {:keys [board] :as game-state} play-turn]
    (let [player-color (whos-turn game-state)
          attack-move (->> (attack-moves board player-color)
                           rand-nth)
          second-move (let [new-board (apply-move board attack-move)]
                        (-> (all-moves new-board player-color)
                            random-move
                            (or core/pass-move)))
          turn (if (first-turn? game-state)
                 [attack-move]
                 [attack-move second-move])]
      (play-turn turn))))

(defrecord AttackThenStackAI []
  tzaar.player/Player
  (-play [_ {:keys [board] :as game-state} play-turn]
    (let [player-color (whos-turn game-state)
          attack-move (->> (attack-moves board player-color)
                           rand-nth)
          second-move (let [new-board (apply-move board attack-move)]
                        (or (random-move (stack-moves new-board player-color))
                            (random-move (attack-moves new-board player-color))
                            core/pass-move))
          turn (if (first-turn? game-state)
                 [attack-move]
                 [attack-move second-move])]
      (play-turn turn))))