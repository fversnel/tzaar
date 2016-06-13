(ns tzaar.player)

(defprotocol Player
  ; Returns two moves of this turn: [{:move-type :attack ...}
  ;                                  {:move-type :stack / :attack / :pass}]
  (play [board]))
