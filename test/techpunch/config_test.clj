(ns techpunch.config-test
  (:require [clojure.test :refer :all]
            [techpunch.config :refer [config]]))

(deftest config-get
  (is (= true (config :test-prop)))
  (is (= true (config :level1 :level2))))
