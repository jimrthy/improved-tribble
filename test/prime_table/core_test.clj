(ns core-test
  (:require [clojure.test :refer (are deftest is)]
            [prime-table.core :as p-t]))

(deftest failure
  (is (= 0 1)))
