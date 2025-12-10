(ns techpunch.num-test
  (:require [clojure.test :refer :all]
            [techpunch.num :refer :all]))

(defmacro example-ez-num-usage [n]
  `(double (ez-num ~n)))

(deftest ez-num-test
  (is (= 0.9 (example-ez-num-usage .9))))
