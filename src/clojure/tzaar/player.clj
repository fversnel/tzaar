(ns tzaar.player
  (require [tzaar.spec :as spec]
           [tzaar.core :as core :refer [color-to-str]]
           [tzaar.util.macros :refer [try-repeatedly]]
           [clojure.spec :as s]
           [clojure.string :as string]))

(defprotocol Player
  (-play [player color board first-turn? play-turn]))

(defn play
  [player color board first-turn? play-turn]
  (-play player color board first-turn?
         (fn [turn]
           (if (and (s/valid? ::spec/turn turn)
                    (core/valid-turn? board color first-turn? turn))
             (play-turn turn)
             (throw (Exception. (str (core/color-to-str color)
                                     " invalidly plays '"
                                     (core/turn-to-str turn)
                                     "' on board:"
                                     \newline
                                     (core/board-to-str board))))))))

(def random-but-legal-ai
  (reify tzaar.player/Player
    (-play [_ color board first-turn? play-turn]
      (let [attack-move (->> (core/all-moves board color)
                             (filter core/attack-move?)
                             shuffle
                             first)
            second-move (let [new-board (core/apply-move board attack-move)]
                          (->  (core/all-moves new-board color)
                               shuffle
                               first
                               (or core/pass-move)))]
        (play-turn [attack-move (if first-turn? core/pass-move second-move)])))))

(def command-line-player
  (letfn [(coordinate-to-position [coordinate-str]
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
          (parse-move [expr] ; Parses "attack A1 A2"
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