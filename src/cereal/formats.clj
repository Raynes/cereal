(ns cereal.formats
  (:import (java.io SequenceInputStream ByteArrayInputStream)
           (java.nio ByteBuffer)
           (clojure.lang SeqEnumeration)))

(defn buf->stream [buf]
  (let [offset (+ (.position buf) (.arrayOffset buf))
        length (- (.limit buf) (.position buf))]
    (ByteArrayInputStream. (.array buf) offset length)))

(defn buf-seq->stream [buf-seq]
  (SequenceInputStream.
   (SeqEnumeration.
    (map buf->stream buf-seq))))

(defn to-buf-seq [& args]
  (for [a args]
    (if (instance? ByteBuffer)
      a
      (ByteBuffer/wrap
       (if (instance? String a)
         (.getBytes a)
         a)))))
