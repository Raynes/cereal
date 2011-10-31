(ns cereal.core
  (:use [gloss.core.protocols :only [Reader Writer]]
        [gloss.core.formats :only [to-buf-seq]]
        [useful.map :only [map-to]]
        [useful.fn :only [fix]]
        [clojure.java.io :only [reader input-stream]])
  (:require io.core gloss.core)
  (:import (java.nio ByteBuffer)
           (java.io PushbackReader InputStreamReader
                    ObjectInputStream ObjectOutputStream ByteArrayOutputStream)))

(defn java-codec [& {:keys [validator repeated]}]
  (-> (reify
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
             (.toByteArray byte-stream)))))
      (fix repeated
           #(gloss.core/repeated (gloss.core/finite-frame :int32 %) :prefix :none))))

(defn- read-seq [in]
  (lazy-seq
   (let [form (read in false ::EOF)]
     (when (not= ::EOF form)
       (cons form (read-seq in))))))

(defn clojure-codec [& {:keys [validator repeated]}]
  (reify
    Reader
    (read-bytes [this buf-seq]
      (let [forms (read-seq (PushbackReader. (reader buf-seq)))]
        (if repeated
          [true forms nil]
          (if (next forms)
            (throw (Exception. "Bytes left over after decoding frame."))
            [true (first forms) nil]))))

    Writer
    (sizeof [this] nil)
    (write-bytes [this _ val]
      (when (and validator (not (validator val)))
        (throw (IllegalStateException. "Invalid value in clojure-codec")))
      (map #(ByteBuffer/wrap (.getBytes (pr-str %)))
           (fix val (not repeated) list)))))
