(ns techpunch.coll-test
  (:require [clojure.test :refer :all]
            [techpunch.coll :refer :all]
            [techpunch.compare :refer [cmp<]]))

(deftest keep-common-objects
  (let [obj-seqs [[{:n 1 :s 2} {:n 2 :s 1} {:n 3 :s 0} {:n 4 :s -1}]
                  [{:n 1 :s 2} {:n 2 :s 1} {:n 4 :s 0}]
                  [{:n 2 :s 2} {:n 4 :s "s"}]]
        expected [[{:n 2 :s 1} {:n 4 :s -1}]
                  [{:n 2 :s 1} {:n 4 :s 0}]
                  [{:n 2 :s 2} {:n 4 :s "s"}]]]
    (is (= expected (apply keep-common :n obj-seqs)))))

(deftest objects-increasing
  (let [increasing [{:n 1 :s 2} {:n 2 :s 1} {:n 3 :s 0}]
        decreasing [{:n 3 :s 0} {:n 2 :s 1} {:n 1 :s 3}]]
    (is (first-last-cmp? cmp< :n increasing))
    (is (not (first-last-cmp? cmp< :n decreasing)))))
