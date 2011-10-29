(ns cereal.core
  (:use [gloss.core.protocols :only [Reader Writer]]
        [gloss.core.formats :only [to-buf-seq]]
        [useful.map :only [map-to]]
        [useful.fn :only [fix]]
        [clojure.java.io :only [reader input-stream]])
  (:require io.core)
  (:import (java.nio ByteBuffer)
           (java.io PushbackReader ObjectInputStream ObjectOutputStream ByteArrayOutputStream)))

(defn java-codec [& {:keys [validator]}]
  (reify
    Reader
    (read-bytes [this buf-seq]
      [true (.readObject (ObjectInputStream. (input-stream buf-seq))) nil])

    Writer
    (sizeof [this] nil)
    (write-bytes [this _ val]
      (when (and validator (not (validator val)))
        (throw (IllegalStateException. "Invalid value in java-codec")))
      (to-buf-seq
       (let [byte-stream (ByteArrayOutputStream.)]
         (.writeObject (ObjectOutputStream. byte-stream) val)
         (.toByteArray byte-stream))))))

(defn clojure-codec [& {:keys [validator]}]
  (reify
    Reader
    (read-bytes [this buf-seq]
      (let [in  (PushbackReader. (reader buf-seq))
            val (try (read in)
                     (catch Exception e ::EOF))]
        (if (= val ::EOF)
          [false nil nil]
          [true val (let [ch (.read in)
                          buf-seq (drop-while #(not (.hasRemaining %)) buf-seq)]
                      (fix buf-seq (not= -1 ch)
                           (partial cons (ByteBuffer/wrap (byte-array [(byte ch)])))))])))

    Writer
    (sizeof [this] nil)
    (write-bytes [this _ val]
      (when (and validator (not (validator val)))
        (throw (IllegalStateException. "Invalid value in clojure-codec")))
      (to-buf-seq
       (.getBytes
        (pr-str val))))))
