(ns techpunch.util
  "Misc utils"
  (:require [techpunch.coll :refer [subvec-if-exists]]))

(defn throw-rte [msg & more]
  (throw (RuntimeException.
          (apply str msg " " (interleave more (repeat " "))))))

(defn throw-illegal-arg [msg & more]
  (throw (IllegalArgumentException.
          (apply str msg " " (interleave more (repeat " "))))))

(defn valid-arg
  "Like an assert but throws IllegalArgumentException and doesn't pay attention to *assert*"
  [requirement & msg-strs]
  (when-not requirement
    (apply throw-illegal-arg msg-strs)))

(defn swap-get-prev!
  "Does an atom swap! and returns get-f applied to the old val."
  [the-atom get-f swap-f & swap-args]
  (let [[old _] (apply swap-vals! the-atom swap-f swap-args)]
    (get-f old)))

(defn swap-rm-get-first!
  "Removes the first item of a vector atom and returns it."
  [vec-atom]
  (swap-get-prev! vec-atom first subvec-if-exists 1))
