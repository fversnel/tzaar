(ns tzaar.core-test
  (:require [clojure.test :refer :all]
            [tzaar.core :refer :all]
            [tzaar.parser :refer :all]))

(deftest test-parser
  (testing "Board parser"
    (is
      (let [board (random-board)]
        (= board (parse-board (board-to-str board)))))))
