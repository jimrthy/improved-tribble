(def project 'prime-table)
(def version "0.1.0-SNAPSHOT")

(set-env! :dependencies   '[[adzerk/boot-test "RELEASE" :scope "test"]
                            [org.clojure/clojure "1.9.0-beta1"]
                            [org.clojure/spec.alpha "0.1.123" :exclusions [org.clojure/clojure]]
                            [org.clojure/test.check "0.10.0-alpha2" :scope "test" :exclusions [org.clojure/clojure]]
                            [org.clojure/tools.nrepl "0.2.13" :exclusions [org.clojure/clojure]]
                            [samestep/boot-refresh "0.1.0" :scope "test" :exclusions [org.clojure/clojure]]]
          :project project
          :resource-paths #{"src"}
          :source-paths   #{"test"})

(task-options!
 aot {:namespace   #{'prime-table.core}}
 pom {:project     project
      :version     version
      :description "Print a table of prime products"
      ;; TODO: Add a real website
      :url         "https://github.com/jimrthy/"
      :scm         {:url "https://github.com/jimrthy/"}
      :license     {"Copyright 2017, James Gatannah"
                    "https://github.com/jimrthy/"}}
 jar {:file        (str "prime-table-" version ".jar")})

(require '[samestep.boot-refresh :refer [refresh]])

(deftask build
  "Build the project locally as a JAR."
  [d dir PATH #{str} "the set of directories to write to (target)."]
  ;; Note that this approach passes the raw command-line parameters
  ;; to -main, as opposed to what happens with `boot run`
  ;; TODO: Eliminate this discrepancy
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (javac) (aot) (pom) (uber) (jar) (target :dir dir))))

(deftask local-install
  "Create a jar to go into your local maven repository"
  [d dir PATH #{str} "the set of directories to write to (target)."]
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (pom) (jar) (target :dir dir) (install))))

(deftask run
  "Run the project."
  [n grid-size GRID int "Size of grid"]
  (require '[prime-table.core :as p-t])
  (let [main (resolve 'p-t/-main)]
    (main grid-size)))

(require '[adzerk.boot-test :refer [test]])
