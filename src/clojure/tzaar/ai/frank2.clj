(ns tzaar.ai.frank2
  (:require [tzaar.core :as core]
            [tzaar.player :as player :refer [Player]]
            [clojure.core.async :as a :refer [>! <! <!! go go-loop
                                              chan put! alts! timeout]]))

(defn iterate-stacks [board color]
  (core/iterate-stacks color board))

(defn attack-moves
  [board color]
  (->> (core/all-moves board color)
       (filter core/attack-move?)))

(def points {:tzaar (/ 1 6)
             :tzarra (/ 1 9)
             :tott (/ 15)})

(defn score-board [player-color board]
  ; TODO Add extra score attributes:
  ;   - Stack sizes vs enemy stack sizes
  ;   -
  ;

  (letfn [(score-stack [stacks]
            (->> stacks
              ; Example: a stack of 2 with a tzaar at the top
              ; will count as 2 tzaars, however each stack-type
              ; is scored on availability a stack type with a lower
              ; availability with generate more points
              (map (fn [[stack-type stacks]]
                     (let [total-pieces (reduce + (map count stacks))]
                       ;(println stack-type " pieces " total-pieces)
                       (* (stack-type points) total-pieces))))
              (reduce +)))
          (score [color]
            (->> color
                 (iterate-stacks board)
                 (map :slot)
                 (group-by core/stack-type)
                 score-stack))]
    (- (score player-color)
       (score (core/opponent-color player-color)))))

(defn possible-turns
  [{:keys [board] :as game-state}]
  (let [player-color (core/whos-turn game-state)]
    (for [attack-move (attack-moves board player-color)
          second-move (cons
                        core/pass-move
                        (if-not (core/first-turn? game-state)
                          (core/all-moves
                            (core/apply-move board attack-move)
                            player-color)
                          []))]
      [attack-move second-move])))

(defn evaluate-turn [board color turn]
  {:turn turn
   :score (score-board color (core/apply-turn board turn))})

(def ^:private paralellism 8)

(def ^:private turn-buffer-size 6000)

(defn find-best-turn [{:keys [board] :as game-state}]
  (let [best-turn-chan (chan 1)]
    (go
      (let [player-color (core/whos-turn game-state)
            turns-chan (chan turn-buffer-size)
            scored-turns (chan turn-buffer-size)]
        (a/onto-chan turns-chan
                     (possible-turns game-state))
        (a/pipeline paralellism
                    scored-turns
                    (map #(evaluate-turn board player-color %))
                    turns-chan)
        (let [best-turn (a/reduce (fn [best-turn scored-turn]
                                    (if (nil? best-turn)
                                      scored-turn
                                      (if (> (:score scored-turn)
                                             (:score best-turn))
                                        scored-turn
                                        best-turn)))
                                  nil
                                  scored-turns)
              best-scored-turn (<! best-turn)]
          (>! best-turn-chan (:turn best-scored-turn)))))
    best-turn-chan))

(defrecord FrankAI []
  Player
  (-play [_ game-state play-turn]
    (go
      (play-turn (<! (find-best-turn game-state))))))

;(defn test-play []
;  (player/play (FrankAI.) {:board core/default-board :turns []}
;               (fn [turn] (println "test turn" turn))))