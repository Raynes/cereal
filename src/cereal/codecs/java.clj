(ns cereal.codecs.java
  (:use [cereal.formats :only [buf-seq->stream]]
        [cereal.core :only [Fields]]
        [gloss.core.protocols :only [Reader Writer]]
        [gloss.core.formats :only [to-buf-seq]]
        [useful.map :only [map-to]]
        [clojure.java.io :only [reader]])
  (:import (java.io ObjectInputStream ObjectOutputStream ByteArrayOutputStream)))

(deftype Java []
  Reader
  (read-bytes [this buf-seq]
    [true (.readObject (ObjectInputStream. (buf-seq->stream buf-seq))) nil])

  Writer
  (sizeof [this] nil)
  (write-bytes [this _ val]
    (to-buf-seq
     (let [byte-stream (ByteArrayOutputStream.)]
       (.writeObject (ObjectOutputStream. byte-stream) val)
       (.toByteArray byte-stream))))

  Fields
  (fields [this] nil)
  (fields [this subfields] nil))

(defn make [] (Java.))