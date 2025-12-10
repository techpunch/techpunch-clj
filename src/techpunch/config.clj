(ns techpunch.config
  (:refer-clojure :exclude [get])
  (:require [cprop.core :refer [load-config]]))

;; Uses cprop's behavior of looking for either a config.edn on the classpath
;; or a path specified by the `conf` sys prop

(def ^:private cached-config
  (delay (atom (load-config))))

(defn reload []
  (reset! @cached-config (load-config)))

(defn config
  "Gets the config value at key-path"
  [& key-path]
  (get-in @@cached-config key-path))
