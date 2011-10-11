(ns cereal.protobuf
  (:use protobuf.core cereal.format))

(deftype ProtobufFormat [proto]
  Format

  (encode [format node]
    (protobuf-dump
     (if (protobuf? node)
       node
       (protobuf proto node))))

  (decode [format data]
    (when data
      (protobuf-load proto data)))

  (decode [format data offset len]
    (when data
      (protobuf-load proto data offset len)))

  (decode-stream [format stream]
    (when stream
      (protobuf-load-stream proto stream)))

  (fields [format]
    (protofields proto))

  (fields [format subfields]
    (apply protofields proto subfields)))

(defn make [proto]
  (ProtobufFormat. (protodef proto)))