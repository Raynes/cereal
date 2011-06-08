(ns cereal.java
  "Java serialization backend."
  (:use [cereal format netstring])
  (:import (java.io ByteArrayInputStream ByteArrayOutputStream
                    ObjectInputStream ObjectOutputStream)))

(defn- unserialize [data]
  (-> data
      str
      .getBytes
      ByteArrayInputStream.
      ObjectInputStream.
      .readObject))

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
      (encode-netstring (String. (.toByteArray byte-stream)))))

  (decode [format data]
    (apply merge-with adjoin
           (map unserialize (decode-netstring data))))

  (decode [format data offset len]
    (apply merge-with adjoin
           (map unserialize
                (decode-netstring data offset len)))))

(defn make [] (JavaFormat.))