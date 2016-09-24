(ns tzaar.server
  (:require [tzaar.player :refer [Player]]
            [tzaar.jsonapi :as jsonapi]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.core.async :as a :refer [>! <! <!! go go-loop
                                              chan put! alts! timeout
                                              pipeline promise-chan close!]]
            [chord.http-kit :refer [with-channel]]
            [org.httpkit.server :refer [run-server]]
            [clojure.core.async :as a]
            [tzaar.game :as game]))

(def config (-> "config.edn" io/resource slurp edn/read-string))

(defn channel-player [ch]
  (reify
    Player
    (-play [_ game-state play-turn]
      (go (>! ch game-state)
          (play-turn (<! ch))))))

(defn wrap-chans [send-xform
                  receive-xform
                  {:keys [send-chan receive-chan]}]
  {:send-chan (let [from (chan)]
                (pipeline 1
                          send-chan
                          send-xform
                          from)
                from)
   :receive-chan (let [to (chan)]
                   (pipeline 1
                             to
                             receive-xform
                             receive-chan)
                   to)})

(def json-chans (partial wrap-chans
                         (map jsonapi/game-state->json)
                         (map jsonapi/json->turn)))

(defn your-handler [req]
  (with-channel req
                ws-ch
                {:read-ch (a/chan 1)
                 :format  :json}
    (go
      (let [{:keys [message]} (<! ws-ch)
            ch-player (channel-player ws-ch)
            ]
        (game/play-game )
        (prn "Message received:" message)
        (>! ws-ch "Hello client from server!")
        (close! ws-ch)))))


(defn create-game [request]


  )

; TODO Whenever someone posts to /create-game
;      We start a websocket and start a game against an ai.
;      We create a websocket player that will we feed into the
;      game and that will be periodically asked to play a move.
;
;      On the client we then render the board each time (play) is called.