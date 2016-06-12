(ns tzaar.parser
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn parse-board [board-str]
  (letfn [(parse-slot [slot-str]
            (case slot-str
              "w1" [:white :tott]
              "w2" [:white :tzarra]
              "w3" [:white :tzaar]
              "b1" [:black :tott]
              "b2" [:black :tzarra]
              "b3" [:black :tzaar]
              "e" :empty
              "n" :none))]
    (let [rows (->> board-str
                    string/split-lines
                    (map string/trim))]
      (vec
        (for [row rows
              :let [slots (string/split row #"\s+")]]
          (->> slots (map parse-slot) vec))))))

(def read-board (comp parse-board slurp io/resource))

(defn board-to-str [board]
  (letfn [(slot-to-str [slot]
            (case slot
              [:white :tott] "w1"
              [:white :tzarra] "w2"
              [:white :tzaar] "w3"
              [:black :tott] "b1"
              [:black :tzarra] "b2"
              [:black :tzaar] "b3"
              :empty "e"
              :none "n"))]
    (let [row-strs (for [row board]
                     (->>
                       row
                       (map slot-to-str)
                       (string/join \space)))]
      (string/join \newline row-strs))))
