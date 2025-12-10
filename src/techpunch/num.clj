(ns techpunch.num
  (:require [clojure.string :as str])
  (:import (java.math RoundingMode)))

;; Simple numberic type conversions

(defprotocol NumConversions
  (^int as-int [x])
  (^long as-long [x])
  (^double as-double [x]))

(extend-protocol NumConversions
  nil
  (as-int [_])
  (as-long [_])
  (as-double [_])

  Number
  (as-int [n] (.intValue n))
  (as-long [n] (.longValue n))
  (as-double [n] (.doubleValue n))

  String
  (as-int [s] (Integer/parseInt s))
  (as-long [s] (Long/parseLong s))
  (as-double [s] (Double/parseDouble s)))


;; Other misc num fns

(defn round-double
  [decimal-places dbl]
  ; Note: for perf critical code, may want to impl a round-fast that
  ; does mul/div tricks instead of conversion to/from bigdec
  (-> dbl
      (bigdec)
      (.setScale (int decimal-places) RoundingMode/HALF_UP)
      (double)))

(defmacro ez-num
  "Allows you to use .9 instead of 0.9 for fast repl typing. Use in other macros,
  for example: (defmacro testn [n] `(double (ez-num ~n)))"
  [num]
  (let [s (str num)]
    (if (str/starts-with? s ".")
      (read-string (str "0" s))
      num)))
