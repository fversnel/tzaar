 (ns tzaar.players.commandline
   (require [tzaar.player :refer [Player]]
            [tzaar.core :refer :all]
            [tzaar.util.macros :refer [try-repeatedly]]
            [clojure.string :as string]))

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
    (->Move (keyword move-type)
            (coordinate-to-position from)
            (coordinate-to-position to))))

(defrecord CommandlinePlayer []
  tzaar.player/Player
  (-play [_ {:keys [board] :as game-state} play-turn]
    (println (color->str (whos-turn game-state))
             "to play: (example: 'attack a1 a2')")
    (let [color (whos-turn game-state)
          validate-move (fn [board first-turn-move? move]
                          (if (valid-move? board color first-turn-move? move)
                            move
                            (throw (Exception. "Invalid move"))))
          attack-move (try-repeatedly
                        (print "Attack move=> ")
                        (flush)
                        (->> (read-line)
                             parse-move
                             (validate-move board true))
                        :on-failure (println "Wrong input, try again"))
          board-after-attack (apply-move board attack-move)
          second-move (if-not (first-turn? game-state)
                        (try-repeatedly
                          (print "Second move=> ")
                          (flush)
                          (->> (read-line)
                               parse-move
                               (validate-move board-after-attack false))
                          :on-failure (println "Wrong input, try again"))
                        pass-move)]
      (play-turn [attack-move second-move]))))
