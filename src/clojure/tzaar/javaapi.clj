(ns tzaar.javaapi
  (require [tzaar.core :as core]
           [tzaar.player :as player]
           [clojure.java.data :refer [from-java to-java]]
           [camel-snake-kebab.core :refer [->kebab-case
                                           ->PascalCase]])
  (:import (tzaar.java Board Slot Slot$Stack Move Move$Attack Move$Stack
                       Piece Position Stack Turn Color Piece$Type Neighbor FinishedGame GameState Stats)
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

(defmethod from-java Neighbor
  [neighbor]
  (core/->Slot
    (from-java (.stack neighbor))
    (from-java (.-position neighbor))))
(defmethod to-java [Neighbor clojure.lang.IRecord]
  [_ neighbor]
  (Neighbor. (to-java Position (:position neighbor))
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
  [_ [first-move second-move]]
  (Turn. (to-java Move first-move)
         (to-java Move second-move)))

(defmethod from-java Board
  [board]
  (vec (for [row (.-slots board)]
    (vec (for [slot row] (from-java slot))))))
(defmethod to-java [Board clojure.lang.APersistentVector]
  [_ board]
  (Board.
    (for [row board]
      (for [slot row]
        (to-java Slot slot)))))

(defmethod to-java [GameState clojure.lang.APersistentMap]
  [_ {:keys [initial-board board turns]}]
  (GameState. (to-java Board initial-board)
              (map #(to-java Turn %) turns)
              (to-java Board board)))

(defmethod to-java [Stats clojure.lang.APersistentMap]
  [_ {:keys [time-taken total-turns]}]
  (Stats. (to-java Duration time-taken)
          total-turns))

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
             (accept [_ turn] (play-turn (from-java turn)))))))

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

(def-api neighbors [Neighbor] [Board board Position position] core/neighbors)
(def-api moves [Move] [Board board Position position] core/moves)
(def-api all-moves [Move] [Board board Color color] core/all-moves)
(def-api apply-move Board [Board board Move move] core/apply-move)
(def-api apply-turn Board [Board board Turn turn] core/apply-turn)
(def-api board->str String [Board board] core/board->str)
(def-api stack-type-missing? Boolean [Board board Color player-color] core/stack-type-missing?)
(def-api lost? Boolean [Board board Color player-color Boolean first-turn-move?] core/lost?)
(def-api random-board Board [] core/random-board)
(def-api default-board Board [] (fn [] core/default-board))