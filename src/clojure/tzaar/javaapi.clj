(ns tzaar.javaapi
  (require [tzaar.core :as core]
           [tzaar.player :as player]
           [clojure.java.data :refer [from-java to-java]]
           [camel-snake-kebab.core :refer [->kebab-case
                                           ->PascalCase]])
  (:import (tzaar.java Board Slot Slot$Stack Move Move$Attack Move$Stack
                       Piece Position Stack Turn Color Piece$Type StackWithPosition FinishedGame GameState Stats)
           (java.time Duration))
  (:gen-class))

(defn enum-to-keyword [^Enum enum]
  (-> enum
      str
      ->kebab-case
      keyword))

(defn keyword-to-enum [^Class clazz k]
  (->> k
       name
       ->PascalCase
       (Enum/valueOf clazz)))

(defmethod from-java Duration
  [duration]
  (.toNanos duration))
(defmethod to-java [Duration java.lang.Number]
  [_ nanos]
  (Duration/ofNanos nanos))

(defmethod from-java Color
  [enum]
  (enum-to-keyword enum))
(defmethod to-java [Color clojure.lang.Keyword]
  [_ color]
  (keyword-to-enum Color color))

(defmethod from-java Piece$Type
  [enum] (enum-to-keyword enum))
(defmethod to-java [Piece$Type clojure.lang.Keyword]
  [_ piece-type]
  (keyword-to-enum Piece$Type piece-type))

(defmethod from-java Piece
  [piece]
  [(from-java (.-color piece))
   (from-java (.-type piece))])
(defmethod to-java [Piece clojure.lang.APersistentVector]
  [_ [piece-color piece-type]]
  (Piece. (to-java Color piece-color)
          (to-java Piece$Type piece-type)))

(defmethod from-java Stack
  [stack]
  (map from-java (vec (.-pieces stack))))
(defmethod to-java [Stack clojure.lang.Sequential]
  [_ stack]
  (Stack. (map #(to-java Piece %) stack)))

(defmethod from-java Position
  [position]
  [(.-x position) (.-y position)])
(defmethod to-java [Position clojure.lang.APersistentVector]
  [_ [x y]]
  (Position. x y))

(defmethod from-java StackWithPosition
  [neighbor]
  (core/->Slot
    (from-java (.stack neighbor))
    (from-java (.-position neighbor))))
(defmethod to-java [StackWithPosition clojure.lang.IRecord]
  [_ neighbor]
  (StackWithPosition. (to-java Position (:position neighbor))
                      (to-java Stack (:slot neighbor))))

(defmethod from-java Slot
  [slot]
  (cond
    (.isEmpty slot) :empty
    (.isNothing slot) :nothing
    :else (from-java (.-stack slot))))
(defmethod to-java [Slot clojure.lang.Keyword]
  [_ slot]
  (case slot
    :empty Slot/Empty
    :nothing Slot/Nothing))
(defmethod to-java [Slot clojure.lang.Sequential]
  [_ slot]
  (Slot$Stack. (to-java Stack slot)))

(defmethod from-java Move
  [move]
  (cond
    (.isPass move) core/pass-move
    :else (core/->Move
            (cond
              (.isAttack move) :attack
              (.isStack move) :stack)
            (from-java (.-from move))
            (from-java (.-to move)))))
(defmethod to-java [Move clojure.lang.IRecord]
  [_ move]
  (condp = (:move-type move)
    :attack (Move$Attack. (to-java Position (:from move))
                          (to-java Position (:to move)))
    :stack (Move$Stack. (to-java Position (:from move))
                        (to-java Position (:to move)))
    :pass Move/Pass))

(defmethod from-java Turn
  [turn]
  [(from-java (.-firstMove turn))
   (from-java (.-secondMove turn))])
(defmethod to-java [Turn clojure.lang.APersistentVector]
  [_ turn]
  (let [first-turn? (= (count turn) 1)
        [first-move second-move] turn]
    (if first-turn?
      (Turn/firstTurn (to-java Move first-move))
      (Turn. (to-java Move first-move)
             (to-java Move second-move)))))

(defmethod from-java Board
  [board]
  (._clojureBoard board))
(defmethod to-java [Board clojure.lang.APersistentVector]
  [_ board]
  (Board. board))

(defmethod to-java [GameState clojure.lang.APersistentMap]
  [_ {:keys [game-id initial-board board turns]}]
  (GameState. game-id
              (to-java Board initial-board)
              (map #(to-java Turn %) turns)
              (to-java Board board)))

(defmethod to-java [Stats clojure.lang.APersistentMap]
  [_ {:keys [time-taken total-turns total-attack-moves
             total-stack-moves total-pass-moves]}]
  (Stats. (to-java Duration time-taken)
          total-turns
          total-attack-moves
          total-stack-moves
          total-pass-moves))

(defmethod to-java [FinishedGame clojure.lang.APersistentMap]
  [_ game]
  (FinishedGame. (to-java Board (:initial-board game))
                 (map #(to-java Turn %) (:turns game))
                 (to-java Color (:winner game))
                 (to-java Stats (get-in game [:stats :white]))
                 (to-java Stats (get-in game [:stats :black]))))

(extend-type tzaar.java.Player
  player/Player
  (-play [this game-state play-turn]
    (.play this
           (to-java GameState game-state)
           (reify java.util.function.Consumer
             (accept [_ turn]
               ; If it's the first turn of the game
               ; we just ignore the second move to
               ; conform to the play-turn contract
               (let [turn (from-java turn)
                     turn (if (core/first-turn? game-state)
                            [(first turn)]
                            turn)]
                 (play-turn turn)))))))

(defmacro def-api
  [name return-type java-args f]
  {:pre [(even? (count java-args))]}
  (let [java-args (partition 2 java-args)
        args (vec (map second java-args))]
    `(defn ~name ~args
       ~(if (vector? return-type)
          `(map #(to-java ~(first return-type) %)
                (apply ~f (map from-java ~args)))
          `(to-java ~return-type
                    (apply ~f (map from-java ~args)))))))

(def-api iterate-stacks [StackWithPosition] [Color color Board board] core/iterate-stacks)
(def-api neighbors [StackWithPosition] [Board board Position position] core/neighbors)
(def-api moves [Move] [Board board Position position] core/moves)
(def-api all-moves [Move] [Board board Color color] core/all-moves)
(def-api apply-move Board [Board board Move move] core/apply-move)
(def-api apply-turn Board [Board board Turn turn] core/apply-turn)
(def-api lookup Slot [Board board Position position] core/lookup)
(def-api board->str String [Board board] core/board->str)
(def-api stack-type-missing? Boolean [Board board Color player-color] core/stack-type-missing?)
(def-api lost? Boolean [Board board Color player-color Boolean first-turn-move?] core/lost?)
(def-api random-board Board [] core/random-board)
(def-api default-board Board [] (fn [] core/default-board))

(defn to-slots [^Board board]
  (for [row (.-clojureBoard board)]
    (for [slot row]
      (to-java Slot slot))))
