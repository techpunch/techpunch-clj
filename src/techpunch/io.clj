(ns techpunch.io
  "Some handy io fns"
  (:refer-clojure :exclude [read-string])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [techpunch.string :as tstr])
  (:import (java.nio.file CopyOption Files LinkOption NoSuchFileException OpenOption
                          Path Paths StandardCopyOption StandardOpenOption)
           (java.io File ByteArrayInputStream PushbackReader)
           (java.nio.file.attribute FileAttribute)))

(defprotocol Conversions
  "Convert between modern java.nio.file.Path & legacy java.io.File"
  (^File as-file [x] "Convert arg to a File. For compat with older pre-java.nio code.")
  (^Path as-path [x] "Convert arg to a Path. Preferred in the post-java.nio world."))

(extend-protocol Conversions
  nil
  (as-file [_])
  (as-path [_])

  String
  (as-file [s] (io/file s))
  (as-path [s] (Paths/get s (make-array String 0)))

  File
  (as-file [f] f)
  (as-path [f] (.toPath f))

  Path
  (as-file [p] (.toFile p))
  (as-path [p] p))


(defn user-home []
  (System/getProperty "user.home"))

(def separator
  File/separator)

(defn expand-home
  "Expands tilde (~) to user.home system property if ~ is first character. Returns
  either a String with the substition, or whatever was passed in if no ~ found."
  [path]
  (let [s (str path)]
    (if (str/starts-with? s "~")
      (str/replace-first s "~" (user-home))
      path)))

(defn path
  "Returns a Path object for given strings. Tilde is expanded to user's home dir
  if present in s."
  [s & more]
  (Paths/get (expand-home s)
             (->> more
                  (map str)
                  (into-array String))))

(defn read-edn-file [path]
  (io!
   (with-open [rdr (io/reader (expand-home path))]
     (edn/read (PushbackReader. rdr)))))

(def ^:private default-link-opts
  (make-array LinkOption 0))

(defn exists? [path]
  (Files/exists (as-path path) default-link-opts))

(def ^:private default-file-attrs
  (make-array FileAttribute 0))

(defn create-dirs
  "Convenience method for calling java.nio.file.Files.createDirectories"
  [path]
  (io! (Files/createDirectories (as-path path) default-file-attrs)))

(def ^:private atomic-move-opts
  (into-array CopyOption [StandardCopyOption/ATOMIC_MOVE]))

(defn move
  "Atomically moves source-path. target-path should include the target filename,
  not just the dir. Creates dirs in target-path if necessary. Most exceptions
  thrown by java.nio.file.Files.move will flow through, except NoSuchFileException.
  Returns the new Path of the file or nil if source-path didn't exist."
  [source-path target-path]
  (let [source-path (as-path source-path)
        target-path (as-path target-path)
        do-move #(io! (Files/move source-path target-path atomic-move-opts))]
    (try
      (do-move)
      (catch NoSuchFileException _
        (when (exists? source-path)
          (create-dirs (.getParent target-path))
          (do-move))))))

(defn stamp
  "Inserts a _<timestamp> before the last dot in path or at the end if no dot exists
  and returns a String."
  [path]
  (let [s (str path)
        i (or (str/last-index-of s ".")
              (count s))]
    (tstr/insert s i "_" (System/currentTimeMillis))))

(defn- archive-file-path
  "Returns a Path for an archive file. Appends a timestamp before the file extension
  or at the end of the filename if no exts."
  [source-file-path archive-subdir]
  (let [source-file-path (as-path source-file-path)
        parent-dir (.getParent source-file-path)
        source-filename (.getFileName source-file-path)]
    (.resolve parent-dir (str archive-subdir
                              (separator)
                              (stamp source-filename)))))

(defn archive
  "Moves the file specified by source-path (if exists) to archive-subdir,
  appending a timestamp to the file's name as described in the stamp fn."
  [source-path archive-subdir]
  (move source-path (archive-file-path source-path archive-subdir)))

(def ^:private ^"[Ljava.nio.file.OpenOption;" write-opts
  (into-array OpenOption [StandardOpenOption/CREATE
                          StandardOpenOption/WRITE
                          StandardOpenOption/TRUNCATE_EXISTING]))

(defn write-path
  "Writes content out to path. The archive-subdir arg may be used if you wish
  to provide a dir name to have existing files archived to. If none is provided,
  existing files are simply overwritten."
  ([path ^bytes bytes]
   {:pre [(bytes? bytes)]}
   (let [path (as-path path)]
     (io! (create-dirs (.getParent path))
          (Files/write path bytes write-opts))))
  ([path bytes archive-subdir]
   (when archive-subdir (archive path archive-subdir))
   (write-path path bytes)))

(defn read-all-bytes
  "Returns a byte array containing contents of path, or nil if file not found.
  Most exceptions thrown by java.nio.file.Files.readyAllBytes will flow through,
  except NoSuchFileException."
  [path]
  (try
    (io! (Files/readAllBytes (as-path path)))
    (catch NoSuchFileException _)))

(defn ^ByteArrayInputStream str-stream [^String s]
  (-> (.getBytes s "UTF-8")
      (ByteArrayInputStream.)))
