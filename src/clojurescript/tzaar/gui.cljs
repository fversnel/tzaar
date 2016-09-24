(ns tzaar.gui
  (:require [tzaar.core :as core]
            [reagent :as r]))

(defn stack [s]
  ; TODO Render number of pieces
  [:img {:src (case (core/top-piece s)
                [:white :tzaar] "images/white-tzaar.svg"
                [:white :tzarra] "images/white-tzaar.svg"
                [:white :tott] "images/white-tzaar.svg"
                [:black :tzaar] "images/black-tzaar.svg"
                [:black :tzarra] "images/black-tzaar.svg"
                [:black :tott] "images/black-tzaar.svg")}])

(defn slot [s]
  (cond
    (= s :empty) "e"
    (= s :n) "n"
    :default (stack s)))

(defn board [b]


  )