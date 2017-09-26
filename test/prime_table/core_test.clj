(ns core-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer (are deftest is)]
            [prime-table.core :as p-t]))

(deftest check-next-prime
  (are [prev existing expected] (= (p-t/next-prime prev existing) expected)
    nil nil 2
    2 nil 3
    3 [2] 5
    5 [2 3] 7
    7 [2 3 5] 11
    11 [2 3 5 7] 13
    13 [2 3 5 7 11] 17
    17 [2 3 5 7 11 13] 19))

(deftest check-prime-generation
  (let [first-3 (take 3 (p-t/primes))]
    (is (= [2 3 5] first-3)))
  (let [next-4 (-> (p-t/primes)
                   (drop 3)
                   (take 4))]
    (is (= [7 11 13 17] next-4))))

(deftest check-prime-predicate
  (let [primes #{2 3 5 7 11 13 17 19 23 29
                 31 37 41 43 47 53 59 61 71}
        test-vals (gen/sample (s/gen primes))]
    (is (every? p-t/prime? test-vals)))
  (let [not-primes #{ -1 0 1 4 6 8 9 10
                     12 14 15 16 18 20 21
                     22 24 25 26 27 28 30
                     32 33 34 35 36 38 39
                     40 42 44 45 46 48 49
                     50 51 52 54 55 56 57
                     58 61 62 63 64 65 66
                     67 68 69 70}
        test-vals (gen/sample (s/gen not-primes))]
    (is (every? (complement p-t/prime? test-vals)))))

(deftest check-prime-calculation
  )
