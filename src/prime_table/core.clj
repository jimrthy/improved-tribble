(ns prime-table.core
  (:require [clojure.edn :as edn]
            [clojure.pprint :refer (print-table)]
            [clojure.spec.alpha :as s]))

(declare prime?)
(s/def ::prime (s/and nat-int?
                      prime?))
(s/def ::primes (s/coll-of prime?))

(s/fdef next-prime
        :args (s/cat :previous ::prime
                     :existing ::primes)
        :ret ::prime)
(defn next-prime
  "Returns the next prime greater than previous, using existing to test"
  [[previous existing]]
  (if previous
    (if existing
      (reduce (fn [_ x]
                (let [sqrt (Math/sqrt x)
                      possible-factors (filter #(<= % sqrt) existing)]
                  ;; It's tempting to skip even numbers here as a performance
                  ;; optimization. Preliminary microbenchmark tests show that
                  ;; the difference is either within the margin of error, or
                  ;; that it might have a negative impact.
                  (when (every? #(not= 0 (rem x %))
                                possible-factors)
                    (reduced [x (conj existing previous)]))))
              ;; Adding the step by 2 here seems to have a
              ;; slight positive impact.
              (range previous ##Inf 2))
      (do
        (assert (= 2 previous))
        [3 [2]]))
    [2 nil]))

(defn primes*
  "This calculates each prime with the state needed to calculate the next"
  []
  (iterate next-prime [2 nil]))

(defn primes
  "Return a lazy infinite sequence of prime numbers"
  []
  (map first (primes*)))

(s/fdef prime?
        :args (s/cat :n nat-int?)
        :ret boolean?)
(defn prime?
  "Returns true if n is prime [compared against ms]"
  ([n]
   (when (< 1 n)
     ;; The fact that we have a circular dependency
     ;; between this and primes is disturbing.
     (let [sqrt (Math/sqrt n)
           ms (take-while #(<= % sqrt) (primes))]
       (prime? n ms))))
  ([n ms]
   (every? #(not= 0 (rem n %)) ms)))

(s/fdef pick-primes
        :args (s/cat :n nat-int?)
        :fn (s/and (fn [{:keys [:ret]
                         {:keys [:n]} :args}]
                     (= n (count ret)))
                   (fn [{:keys [:ret]}]
                     (every? prime? ret)))
        :ret (s/coll-of nat-int?))
(defn pick-primes
  "Returns a seq of the first n primes"
  [n]
  (take n (primes)))

(defn build-prime-table
  "Build a matrix of the products of n primes"
  [n]
  (let [ms (take n (primes))
        header (concat [:P] ms)]
    {::header header
     ::body
     (map (fn [row-mult]
            (let [multiplier (partial * row-mult)]
              (reduce (fn [acc col-mult]
                        (assoc acc col-mult (multiplier col-mult)))
                      {:P row-mult}
                      ms)))
          ms)}))
(comment
  (build-prime-table 3))

(defn display
  [{:keys [::header ::body]
    :as table}]
  (print-table header body))

(defn -main
  "Main entry point"
  [n]
  (let [table (build-prime-table n)]
    (display table)))

(comment
  ;; There isn't a great way to test this part
  ;; automatically. Leaving this here as an
  ;; easy way to do a visual scan.
  (-main 4)
  (-main 10)
  (-main 20))
