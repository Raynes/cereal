(ns cereal.codecs.clojure
  (:use [cereal.formats :only [buf-seq->stream]]
        [cereal.core :only [Fields]]
        [gloss.core.protocols :only [Reader Writer]]
        [gloss.core.formats :only [to-buf-seq]]
        [useful.map :only [map-to]]
        [clojure.java.io :only [reader]])
  (:import (java.io PushbackReader)))

(deftype Clojure []
  Reader
  (read-bytes [this buf-seq]
    (prn buf-seq)
    (binding [*in* (PushbackReader. (reader (buf-seq->stream buf-seq)))]
      [true
       (read)
       (do (.close *in*)
           (prn (seq (drop-while #(not (.hasRemaining %)) buf-seq)))
           (seq (drop-while #(not (.hasRemaining %)) buf-seq)))]))

  Writer
  (sizeof [this] nil)
  (write-bytes [this _ val]
    (to-buf-seq
     (.getBytes
      (pr-str val))))

  Fields
  (fields [this] nil)
  (fields [this subfields] nil))

(defn make [&]
  (Clojure.))