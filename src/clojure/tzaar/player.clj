(ns tzaar.player
  (require [tzaar.spec :as spec]
           [tzaar.core :as core :refer [color-to-str]]
           [tzaar.util.macros :refer [try-repeatedly]]
           [clojure.spec :as s]
           [clojure.string :as string])
  (:import (tzaar.core Move)))

(defprotocol Player
  (-play [player game-state play-turn]))

(defn play
  [player game-state play-turn]
  (-play player game-state
         (fn [turn]
           {:pre [(not (some nil? turn))
                  (s/valid? ::spec/turn turn)]}
           (if (core/valid-turn? game-state turn)
             (play-turn turn)
             (throw (Exception. (str (core/color-to-str (core/whos-turn game-state))
                                     " invalidly plays '"
                                     (core/turn-to-str turn)
                                     "' on board:"
                                     \newline
                                     (core/board-to-str (:board game-state)))))))))

(defn- random-move [moves]
  (if-not (empty? moves)
    (rand-nth moves)
    core/pass-move))

(defrecord RandomButLegalAI []
  tzaar.player/Player
  (-play [_ {:keys [board] :as game-state} play-turn]
    (let [player-color (core/whos-turn game-state)
          attack-move (->> (core/all-moves board player-color)
                           (filter core/attack-move?)
                           random-move)
          second-move (let [new-board (core/apply-move board attack-move)]
                        (-> (core/all-moves new-board player-color)
                            random-move))]
      (play-turn [attack-move (if (core/first-turn? game-state)
                                core/pass-move
                                second-move)]))))

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
(defn- parse-move [expr] ; Parses "attack A1 A2"
            (let [[move-type from to] (-> expr
                                          string/trim-newline
                                          (string/split #"\s+"))]
              (Move. (keyword move-type)
                     (coordinate-to-position from)
                     (coordinate-to-position to))))

(defrecord CommandlinePlayer []
  tzaar.player/Player
  (-play [_ {:keys [board] :as game-state} play-turn]
    (println (color-to-str (core/whos-turn game-state))
             "to play: (example: 'attack a1 a2')")
    (let [color (core/whos-turn game-state)
          validate-move (fn [board first-turn-move? move]
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
          second-move (if-not (core/first-turn? game-state)
                        (try-repeatedly
                          (print "Second move=> ")
                          (flush)
                          (->> (read-line)
                               parse-move
                               (validate-move board-after-attack false))
                          :on-failure (println "Wrong input, try again"))
                        core/pass-move)]
      (play-turn [attack-move second-move]))))
