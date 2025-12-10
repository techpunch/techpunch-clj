(ns techpunch.java
  (:import (java.lang.management ManagementFactory))
  (:require [clojure.tools.logging :as log]))

(defn ^"[Ljava.lang.String;" string-array
  "Creates a Java String[] with correct type hinting where needed to
  make compiler happy."
  ([] (make-array String 0))
  ([& strings] (into-array String strings)))

(defn thread-mx-bean []
  (ManagementFactory/getThreadMXBean))

(defn thread-count []
  (.getThreadCount (thread-mx-bean)))

(defn thread
  "Creates a thread to run fn f with the current bindings retained."
  [f]
  (Thread. (bound-fn* f)))

(defn add-shutdown-hook [f]
  (->> (thread f)
       (.addShutdownHook (Runtime/getRuntime))))

(defn install-thread-count-logger
  "Runs a future that monitors for changes in the Java thread count and logs
  them."
  ([& {:keys [log-level poll-interval-ms]
       :or {log-level :debug poll-interval-ms 5000}}]
   (future
     (loop [last (thread-count)]
       (let [curr (thread-count)]
         (when (not= last curr)
           (log/logf log-level "[ThreadCount] count=%d peak=%d"
                     curr (.getPeakThreadCount (thread-mx-bean))))
         (Thread/sleep poll-interval-ms)
         (recur curr))))))
