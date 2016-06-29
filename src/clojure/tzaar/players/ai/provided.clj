(ns tzaar.players.ai.provided
  (:require [tzaar.player :refer [Player]]
            [tzaar.core :refer :all]))

(defn- random-move [moves]
  (if-not (empty? moves)
    (rand-nth moves)
    pass-move))

(defrecord RandomButLegalAI []
  tzaar.player/Player
  (-play [_ {:keys [board] :as game-state} play-turn]
    (let [player-color (whos-turn game-state)
          attack-move (->> (all-moves board player-color)
                           (filter attack-move?)
                           random-move)
          second-move (let [new-board (apply-move board attack-move)]
                        (-> (all-moves new-board player-color)
                            random-move))]
      (play-turn [attack-move (if (first-turn? game-state)
                                pass-move
                                second-move)]))))