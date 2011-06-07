(ns cereal.protobuf
  "Protobuf backend."
  (:use protobuf cereal.format))

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
    (map first (protofields proto))))

(defn make [proto]
  (ProtobufFormat. (protodef proto)))