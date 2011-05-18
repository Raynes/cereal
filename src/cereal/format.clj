(ns cereal.format)

(defprotocol Format
  (encode [format data] "Serialize some data.")
  (decode [format data] [format data offset len] "Unserialize some data.")
  (fields [format]))