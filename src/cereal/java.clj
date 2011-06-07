(ns cereal.java
  "Java serialization backend."
  (:use cereal.format
        gloss.core)
  (:require [gloss.io :as io])
  (:import (java.io ByteArrayInputStream ByteArrayOutputStream
                    ObjectInputStream ObjectOutputStream)
           java.nio.ByteBuffer))

(defn- unserialize [data]
  (-> data
      str
      .getBytes
      ByteArrayInputStream.
      ObjectInputStream.
      .readObject))

(def netstring
  (finite-frame
   (prefix
    (string-integer :ascii :delimiters [":"])
    inc
    dec)
   (string :utf-8 :suffix ",")))

(defn adjoin
  "Same as adjoin from clojure-useful, but works with screwed up booleans."
  [left right]
  (cond (map? left)
        (merge-with adjoin left right)

        (and (set? left) (map? right))
        (reduce
         (fn [set [k v]]
           ((if (Boolean/valueOf v) conj disj) set k))
         left right)

        (coll? left)
        ((if (coll? right) into conj) left right)

        :else right))

(deftype JavaFormat []
  Format
  
  (encode [format data]
    (let [byte-stream (ByteArrayOutputStream.)]
      (.writeObject (ObjectOutputStream. byte-stream) data)
      (.array
       (io/contiguous
        (io/encode netstring (String. (.toByteArray byte-stream)))))))

  (decode [format data]
    (apply merge-with adjoin
           (map unserialize (io/decode-all netstring data))))

  (decode [format data offset len]
    (apply merge-with adjoin
           (map unserialize
                (io/decode-all netstring
                               (ByteBuffer/wrap data offset len))))))

(defn make [] (JavaFormat.))