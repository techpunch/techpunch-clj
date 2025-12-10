(ns techpunch.util-test
  (:require [clojure.test :refer :all]
            [techpunch.util :refer :all]))

(deftest swap-gets
  (testing "swap-get-rm-first!"
    (let [vec-atom (atom [1 2 3])
          val (swap-rm-get-first! vec-atom)]
      (is (= 1 val))
      (is (= [2 3] @vec-atom))))
  (testing "swap-get-rm-first! empty vec"
    (let [vec-atom (atom [])
          val (swap-rm-get-first! vec-atom)]
      (is (nil? val))
      (is (nil? @vec-atom)))))
