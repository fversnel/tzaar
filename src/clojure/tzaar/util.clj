(ns tzaar.util)

(defmacro try-repeatedly
  [& body]
  (let [[try-body failure-body] (split-with #(not= % :on-failure) body)
        failure-body (remove #(= % :on-failure) failure-body)]
    `(loop []
      (let [[result# exception#]
            (try
              [(do ~@try-body) nil]
              (catch Exception e#
                ~@failure-body
                [nil e#]))]
        (if-not exception# result# (recur))))))