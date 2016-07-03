;(ns tzaar.jsonapi
;  (require [tzaar.player :refer [Player]])
;  (:import (tzaar.java JsonPlayer)))
;
;(extend-type JsonPlayer
;  Player
;  (-play [this game-state play-turn]
;    (.play this
;          (to-json game-state)
;          (reify java.util.function.Consumer
;            (accept [_ turn]
;                (play-turn (from-json turn)))))))