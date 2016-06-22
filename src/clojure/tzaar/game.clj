(ns tzaar.game
  (require [tzaar.core :as core :refer [color-to-str]]
           [tzaar.player :refer [play]]
           [tzaar.util.logging :as logger]
           [clojure.string :as string]
           [clojure.core.async :refer [>! <! <!! go go-loop
                                       chan put! alts! timeout]])
  (:import (tzaar.util.logging Logger)))

(defn- position-to-coordinate [[x y]]
  (let [column (string/upper-case (char (+ x (int \a))))
        row (+ y 1)]
    (str column row)))

(defn- move-to-str [move]
  (case (:move-type move)
    :attack (str (position-to-coordinate (:from move))
                 " attacks "
                 (position-to-coordinate (:to move)))
    :stack (str (position-to-coordinate (:from move))
                " stacks "
                (position-to-coordinate (:to move)))
    :pass "passes"))

(defn- flip-color [color]
  (if (= color :white) :black :white))

(defn play-game
  [white-player black-player board ^Logger l]
  (let [done-chan (chan 1)
        initial-board board]
    (go-loop [board board
              [player-color & colors] (cycle [:white :black])
              [player & players] (cycle [white-player black-player])
              turns []]
      (logger/writeln l (core/board-to-str board) \newline)
      (if-not (core/lost? board player-color true)
        (let [turn-chan (chan 1)]
          (play
            player
            player-color
            board
            (empty? turns)
            (fn [turn] (put! turn-chan turn)))
          (let [turn (<! turn-chan)]
            (logger/writeln l
                            "Turn" (str (inc (count turns)) ":")
                            (color-to-str player-color)
                            "plays"
                            (string/join ", then " (map move-to-str turn)))
            (recur (core/apply-turn board turn)
                   colors
                   players
                   (conj turns turn))))
        (let [winner (flip-color player-color)]
          (logger/writeln l
                          (color-to-str winner)
                          "wins after" (count turns) "turns")
          (>! done-chan {:initial-board initial-board
                         :turns turns
                         :winner winner}))))
    (<!! done-chan)))

