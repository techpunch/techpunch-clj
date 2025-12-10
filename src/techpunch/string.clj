(ns techpunch.string
  (:require [clojure.edn :as edn]
            [clojure.string :as str]))

(defn insert
  "Inserts vals into s at index."
  [s index & vals]
  (str (subs s 0 index)
       (str/join vals)
       (subs s index)))

(defn digit?
  [^Character c]
  (Character/isDigit c))

(defn ratio
  "Flexible string-to-ratio conversion. Can handle ratios like 2:1 or 2-1.
  Returns a Number or nil if s couldn't be parsed into a Number."
  [s]
  (let [r (edn/read-string (str/replace s #"[:-]" "/"))]
    (when (number? r)
      r)))
