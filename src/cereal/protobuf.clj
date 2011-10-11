(ns cereal.protobuf
  (:use protobuf.core cereal.format))

(deftype ProtobufFormat [proto]
  Format

  (encode [format node]
    (protobuf-dump
     (if (protobuf? node) node (protobuf proto node))))

  (decode [format data]
    (if data (protobuf-load proto data)))

  (decode [format data offset len]
    (if data (protobuf-load proto data offset len)))

  (fields [format]
    (protofields proto))

  (fields [format subfields]
    (apply protofields proto subfields)))

(defn make [proto]
  (ProtobufFormat. (protodef proto)))