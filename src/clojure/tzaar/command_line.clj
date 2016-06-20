(ns tzaar.command-line
  (require [tzaar.core :as core]
           [tzaar.util :refer [try-repeatedly]]
           [tzaar.player :refer [play]]
           [clojure.string :as string]
           [clojure.core.async
            :as a
            :refer [>! <! <!! go go-loop chan put! alts! timeout]]))

(defn- color-to-str [color]
  (string/capitalize (name color)))

(defn- coordinate-to-position [coordinate-str]
  (let [[column-char row-char] coordinate-str
        x (-> column-char
              string/lower-case
              first
              int
              (- (int \a)))
        y (-> row-char
              str
              Integer/parseInt
              (- 1))]
    [x y]))

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

(defn command-line-game [white-player black-player board]
  (let [done-chan (chan)]
    (go-loop [board board
              [player-color & colors] (cycle [:white :black])
              [player & players] (cycle [white-player black-player])
              turns []]
      (println (core/board-to-str board) \newline)
      (if-not (core/lost? board player-color true)
        (do
          (let [turn-chan (chan 1)]
            (play
              player
              player-color
              board
              (empty? turns)
              (fn [turn] (put! turn-chan turn)))
            (let [turn (<! turn-chan)]
              (println "Turn" (str (inc (count turns)) ":")
                       (color-to-str player-color)
                       "plays"
                       (string/join ", then " (map move-to-str turn)))
              (recur (core/apply-turn board turn)
                     colors
                     players
                     (conj turns turn)))))
        (do
          (let [winner (flip-color player-color)]
            (println (color-to-str winner)
                     "wins after" (count turns) "turns")
            (>! done-chan winner)))))
    (<!! done-chan)))

(def command-line-player
  (letfn [(parse-move [expr] ; Parses "attack A1 A2"
            (let [[move-type from to] (-> expr
                                          string/trim-newline
                                          (string/split #"\s+"))]
              {:move-type (keyword move-type)
               :from (coordinate-to-position from)
               :to (coordinate-to-position to)}))]
    (reify tzaar.player/Player
      (-play [_ color board first-turn? play-turn]
        (println (color-to-str color) "to play: (example: 'attack a1 a2')")
        (let [validate-move (fn [board first-turn-move? move]
                              (if (core/valid-move? board color first-turn-move? move)
                                move
                                (throw (Exception. "Invalid move"))))
              attack-move (try-repeatedly
                            (print "Attack move=> ")
                            (flush)
                            (->> (read-line)
                                 parse-move
                                 (validate-move board true))
                            :on-failure (println "Wrong input, try again"))
              board-after-attack (core/apply-move board attack-move)
              second-move (if-not first-turn?
                            (try-repeatedly
                              (print "Second move=> ")
                              (flush)
                              (->> (read-line)
                                   parse-move
                                   (validate-move board-after-attack false))
                              :on-failure (println "Wrong input, try again"))
                            core/pass-move)]
          (play-turn [attack-move second-move]))))))