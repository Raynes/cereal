(ns cereal.netstring
  (:use [gloss core io])
  (:import java.nio.ByteBuffer))

(def netstring
  (finite-frame
   (prefix
    (string-integer :ascii :delimiters [":"])
    inc
    dec)
   (string :utf-8 :suffix ",")))

(defn encode-netstring
  [data]
  (-> (encode netstring data) contiguous .array))

(defn decode-netstring
  ([data] (decode-all netstring data))
  ([data off len]
     (decode-all netstring (ByteBuffer/wrap data off len))))