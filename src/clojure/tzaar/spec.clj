(ns tzaar.spec
  (require [clojure.spec :as s]
           [tzaar.core :as core]))

(s/def ::position (s/tuple integer? integer?))
(s/def ::from ::position)
(s/def ::to ::position)
(s/def ::move-type core/move-types)
(s/def ::attack-move (s/and (s/keys :req-un [::from ::to ::move-type])
                            core/attack-move?))
(s/def ::stack-move (s/and (s/keys :req-un [::from ::to ::move-type])
                           core/stack-move?))
(s/def ::pass-move (s/and (s/keys :req-un [::move-type])
                          core/pass-move?))
(s/def ::move (s/or :attack ::attack-move
                    :stack ::stack-move
                    :pass ::pass-move))

(s/def ::turn (s/tuple ::attack-move ::move))

(s/def ::piece (s/tuple #{:white :black} core/stack-types))
(defn piece-color [piece] (first piece))
(s/def ::stack (s/and (s/+ ::piece)
                      #(apply = (map piece-color %))
                      sequential?))

(s/def ::slot-type (s/or :stack ::stack
                         :other #{:empty :nothing}))
(s/def ::board (s/* (s/* ::slot-type)))