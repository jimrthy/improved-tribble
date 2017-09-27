(ns prime-table.core
  (:require [clojure.edn :as edn]
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
        ;; Use the nil as a placeholder
        row-1 (concat [nil] ms)]
    (concat [row-1]   ; top header
            (map (fn [row-mult]
                   (concat [row-mult]  ; left column
                           ;; And then the cells in the row
                           (map (partial * row-mult) ms)))
                 ms))))

(defn display
  [table]
  (doseq [row table]
    ;; Q: Is it worth trying to automatically generate
    ;; this as ASCII art and do fancy things with the
    ;; formatting to make it actually lay out in a
    ;; nice grid?
    (println row)))

(defn implementation
  "Main entry point"
  ;; Distinct from -main for the sake of REPL testing
  [n]
  (let [table (build-prime-table n)]
    (display table)))
(comment
  ;; There isn't a great way to test this part
  ;; automatically. Leaving this here as an
  ;; easy way to do a visual scan.
  (implementation 4)
  (implementation 10))

(defn -main
  "Really just for collecting command-line arguments"
  [& args]
  (let [n (if-let [n (edn/read-string (first args))]
            n
            10)]
    (implementation n)))
