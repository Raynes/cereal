(ns cereal.java
  (:use cereal.format
        [clojure.string :only [join]])
  (:import (java.io ByteArrayInputStream ByteArrayOutputStream
                    ObjectInputStream ObjectOutputStream)
           java.nio.ByteBuffer))

(defn- unserialize [^String data]
  (-> data
      .getBytes
      ByteArrayInputStream.
      ObjectInputStream.
      .readObject))

(defn netstring
  "Create a netstring."
  [message] (str (count message) ":" message ","))

(defn parse-netstring [^String b]
  (loop [acc [] s b]
    (if (seq s)
      (let [[len data] (.split s ":" 2)
            [top bottom] (split-at (Integer/parseInt len) data)]
        (recur (->> top join (conj acc))
               (-> bottom rest join)))
      acc)))

(defn adjoin
  "Same as adjoin from clojure-useful, but works with screwed up booleans."
  [left right]
  (cond (map? left)
        (merge-with adjoin left right)

        (and (set? left) (map? right))
        (reduce
         (fn [set [k v]]
           ((if (Boolean/valueOf ^Boolean v) conj disj) set k))
         left right)

        (coll? left)
        ((if (coll? right) into conj) left right)

        :else right))

(deftype JavaFormat []
  Format

  (encode [format data]
    (let [byte-stream (ByteArrayOutputStream.)]
      (.writeObject (ObjectOutputStream. byte-stream) data)
      (.getBytes ^String (netstring (String. (.toByteArray byte-stream))))))

  (decode [format data]
    (apply merge-with adjoin
           (map unserialize (parse-netstring (String. ^bytes data)))))

  (decode [format data offset len]
    (apply merge-with adjoin
           (map unserialize
                (String. ^bytes data ^Integer offset ^Integer len)))))

(defn make [] (JavaFormat.)) 
