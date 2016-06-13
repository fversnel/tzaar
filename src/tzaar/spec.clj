(ns tzaar.spec
  (require [clojure.spec :as s]
           [tzaar.core :as core]))

(s/def ::position (s/cat integer? integer?))
(s/def ::from ::position)
(s/def ::to ::position)
(s/def ::move-type core/move-types)
(s/def ::move (s/or (s/and (s/keys :req-un [::from ::to ::move-type])
                           #(not= core/pass-move? %))
                    (s/and (s/keys :req-un [::move-type])
                           #(= core/pass-move? %))))

(s/def ::stack-type core/stack-types)
(s/def ::color #{:white :black})
(s/def ::piece (s/cat ::color ::stack-type))
(s/def ::stack (s/and (s/+ ::piece)
                      #(apply = (map core/stack-color %))))

(s/def ::slot-type #{::stack :empty :nothing})
(s/def ::board (s/* (s/* ::slot-type)))