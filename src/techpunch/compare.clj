(ns techpunch.compare)

;; Flexible less-than/greater-than fns

(defn cmp<
  "Returns non-nil if the Comparable args are in increasing order, otherwise false."
  ([x] true)
  ([x y] (neg? (compare x y)))
  ([x y & more]
   (reduce #(if (neg? (compare %1 %2))
              %2
              (reduced false))
           (list* x y more))))

(defn cmp<=
  "Returns non-nil if the Comparable args are in increasing or equal order, otherwise false."
  ([x] true)
  ([x y] (< (compare x y) 1))
  ([x y & more]
   (reduce #(if (< (compare %1 %2) 1)
              %2
              (reduced false))
           (list* x y more))))

(defn cmp>
  "Returns non-nil if the Comparable args are in decreasing order, otherwise false."
  ([x] true)
  ([x y] (pos? (compare x y)))
  ([x y & more]
   (reduce #(if (pos? (compare %1 %2))
              %2
              (reduced false))
           (list* x y more))))

(defn cmp>=
  "Returns non-nil if the Comparable args are in increasing or equal order, otherwise false."
  ([x] true)
  ([x y] (> (compare x y) -1))
  ([x y & more]
   (reduce #(if (> (compare %1 %2) -1)
              %2
              (reduced false))
           (list* x y more))))
