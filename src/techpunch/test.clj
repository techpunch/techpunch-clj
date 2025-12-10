(ns techpunch.test
  (:require [clojure.test :refer :all]))


(defn fail [msg]
  (is (:see :msg) msg))


;; clojure.test Extensions, remember to include this ns to get activate

; Extension: each?
; Example usage: (is (each? = expected-coll actual-coll))
; Tests a collection of expected results against a collection of actual results,
; using a predicate function for each pair of expected elements. Reports a failure
; for each unsatified pair.

(declare each?) ; declaring squelches "can't resolve" warn when using

(defmethod clojure.test/assert-expr 'each? [msg form]
  `(let [pred# ~(nth form 1)
         expecteds# ~(nth form 2)
         actuals# ~(nth form 3)]
     (if (and (not-empty expecteds#) (empty? actuals#))
       (do-report {:type :fail, :message ~msg
                   :expected '(not-empty ~(nth form 3)),
                   :actual (not-empty actuals#)})
       (let [failures# (->> (interleave expecteds# actuals#)
                            (partition 2)
                            (filter #(not (pred# (first %1) (second %1)))))]
         (if (empty? failures#)
           (do-report {:type :pass, :message ~msg,
                       :expected expecteds#, :actual actuals#})
           (doseq [failure# failures#]
             (do-report {:type :fail, :message ~msg,
                         :expected (first failure#), :actual (second failure#)})))
         failures#))))
