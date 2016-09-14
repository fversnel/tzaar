(ns tzaar.rules
  (:require [tzaar.core :refer :all]
            [clojure.string :as string]))

;; TODO adding a move is rather complex procedure
;;      First we need to check which turn to update
;;      If it is the first turn it only requires one move
;;      If it is not the first turn it requires two moves
;;      At any point a resignation can be applied

(defn turn-complete? [{:keys [turns]}]
  (let [last-turn (last turns)
        first-turn? (= (count turns) 1)]
    (and
      (not-empty turns)
      (if first-turn?
        (= (count last-turn) 1)
        (= (count last-turn) 2)))))

(defn apply-move
  [game-state {:keys [from to move-type] :as move}]
  (if-not (pass-move? move)
    (let [board (:board game-state)
          turns (:turns game-state)
          from-stack (lookup board from)
          to-stack (lookup board to)
          new-stack (case move-type
                      :attack from-stack
                      :stack (concat from-stack to-stack))
          active-turn (if (turn-complete? turns)
                        []
                        ()
                          :default (conj last-turn move)))]
      (assoc game-state
        :board (-> board
                   (update-position from :empty)
                   (update-position to new-stack))
        :turns (conj (drop-last turns) active-turn)))
    game-state))

(defn whos-turn [{:keys [turns]}]
  (or ())
  (if (even? (count turns)) :white :black))

(defn valid-move?
  [game-state move]
  (let [board (:board game-state)
        first-move-in-turn? ()
        active-player (whos-turn game-state)]
    (or (pass-move? move)
        ; TODO If it is the second move in a turn
        ;      we have to make sure that it is not the
        ;      first turn of a game
        (and ((moves board (:from move)) move)
             (or (not first-move-in-turn?) (attack-move? move))
             (= active-player (stack-color (lookup board (:from move))))))))

(defn apply-turn [game-state turn]
  (if-not (resignation? turn)
    (reduce apply-move game-state turn)
    (update-in game-state [:turns] #(conj % turn))))

(defn valid-turn?
  [game-state turn]
  (or
    (resignation? turn)
    (let [[first-move second-move] turn]
      (and
        (valid-move? game-state first-move)
        (valid-move? (apply-move game-state first-move) second-move)))))
        ;(if-not (first-turn? game-state)
        ;  (valid-move? (apply-move board first-move)
        ;               player-color
        ;               false
        ;               second-move)
        ;  (nil? second-move))))))

(defn game-over? [{:keys [board turns] :as game-state}]
  (let [active-player (whos-turn game-state)
        opponent (opponent-color active-player)
        no-attack-moves? (-> (all-moves game-state)
                             (filter attack-move?)
                             empty?)
        winner (fn [color win-condition] {:winner color
                                          :win-condition win-condition})]
    (cond
      no-attack-moves?
        (winner opponent :no-moves)
      (stack-type-missing? board active-player)
        (winner opponent :missing-stack-type)
      (resignation? (last turns))
        (winner active-player :resignation))))