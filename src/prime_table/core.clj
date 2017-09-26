(ns prime-table.core
  (:require [clojure.edn :as edn]
            [clojure.spec.alpha :as s]))

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
