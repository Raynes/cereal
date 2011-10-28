(ns cereal.codecs.protobuf
  (:use protobuf.core
        [cereal.formats :only [buf-seq->stream]]
        [cereal.core :only [Fields]]
        [gloss.core.protocols :only [Reader Writer]]
        [gloss.core.formats :only [to-buf-seq]]))

(deftype Protobuf [proto]
  Reader
  (read-bytes [this buf-seq]
    [true (protobuf-load-stream proto (buf-seq->stream buf-seq)) nil])

  Writer
  (sizeof [this] nil)
  (write-bytes [this _ val]
    (to-buf-seq
     (protobuf-dump
      (if (protobuf? val)
        val
        (protobuf proto val)))))

  Fields
  (fields [this]
    (protofields proto))
  (fields [this subfields]
    (apply protofields proto subfields)))

(defn make [proto]
  (Protobuf. (protodef proto)))