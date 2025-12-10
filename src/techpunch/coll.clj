(ns techpunch.coll
  "Fns & add-ons for working with collections"
  (:require [clojure.set :as set])
  (:import (clojure.lang PersistentQueue)))

(defmacro simple-transducer
  "Returns a transducer where the 0- and 1-arity do the standard boilerplate that
  most transducers do. Uses body for the 2-arity version."
  [[rf result input] & body]
  `(fn [~rf]
     (fn
       ([] (~rf))
       ([~result] (~rf ~result))
       ([~result ~input] ~@body))))

(defn queue
  "Creates a clojure.lang.PersistentQueue from either nothing, a coll or item(s)."
  ([]
   PersistentQueue/EMPTY)
  ([x]
   (if (coll? x)
     (into (queue) x)
     (conj (queue) x)))
  ([x & xs]
   (apply conj (queue) x xs)))

(defn select-indexes
  "Returns a seq containing only the elements in coll corresponding to indexes."
  [coll & indexes]
  (map val
       (select-keys (vec coll) indexes)))

(defn subvec-if-exists [v start]
  (when (>= (count v) start)
    (subvec v start)))

(defn keep-common
  "Keeps an object only if there's an object in all obj-seqs that have the same value
  returned when property-fn is applied to it. Returns a seq of seqs."
  [property-fn & obj-seqs]
  (let [prop-sets (map #(set (map property-fn %)) obj-seqs)
        common-prop-set (apply set/intersection prop-sets)
        filter-fn (partial filter #(common-prop-set (property-fn %)))]
    (map filter-fn obj-seqs)))

(defn first-last-cmp?
  "Compares the first & last obj in obj-vec. Returns the result of applying
  cmp-fn to first & last of obj-vec."
  [cmp-fn property-fn obj-vec]
  {:pre [(vector? obj-vec)]}
  (let [prop1 (property-fn (first obj-vec))
        prop2 (property-fn (peek obj-vec))]
    (cmp-fn prop1 prop2)))

(defmacro locals-map
  "Creates a map using the symbol names (as keywords) as keys and the resolved
  local value of each symbol as corresponding values. e.g. (locals-map x y) =>
  {:x x :y y}"
  [& syms]
  (zipmap (map keyword syms) syms))
