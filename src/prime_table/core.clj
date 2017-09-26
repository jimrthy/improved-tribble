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
  [previous existing]
  (println "Looking for the next prime after" previous)
  (if previous
    (if existing
      (reduce (fn [_ x]
                (println (str "Does " x " satisfy? (vs "
                              existing ")"))
                (let [sqrt (Math/sqrt x)
                      possible-factors (filter #(<= % sqrt) existing)]
                  (println "Comparing against" possible-factors)
                  ;; Filtering out even numbers is cheap.
                  ;; TODO: Verify that it was worth it.
                  (when (and (odd? x)
                             (every? #(not= 0 (rem x %))
                                     possible-factors))
                    (reduced x))))
              (range previous ##Inf))
      (do
        (assert (= 2 previous))
        3))
    2))

(defn primes
  "Infinite seq of prime numbers"
  ([]
   (primes nil nil))
  ([previous existing]
   (let [current (next-prime previous existing)]
     (lazy-seq (cons (conj (list previous) existing)
                     (primes current (if previous
                                       (if existing
                                         (cons previous existing)
                                         [previous])
                                       nil)))))))
(comment
  (first (primes))
  (take 3 (primes))
  )

(s/fdef prime?
        :args (s/cat :n nat-int?)
        :ret boolean?)
(defn prime?
  "Returns true if n is prime [compared against ms]"
  ([n]
   ;; The fact that we have a circular dependency
   ;; between this and primes is disturbing.
   (let [sqrt (Math/sqrt n)
         ms (take-while #(< sqrt %) (primes))]))
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
  (throw (RuntimeException. "Write this")))

(defn build-prime-table
  "Build a matrix of the products of n primes"
  [n]
  (throw (RuntimeException. "Write this")))

(defn display
  [table]
  (throw (RuntimeException. "Write this")))

(defn -main
  [& args]
  (let [n (if-let [n (edn/read-string (first args))]
            n
            10)
        table (build-prime-table n)]
    (display table)))
