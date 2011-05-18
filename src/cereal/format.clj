(ns cereal.format)

(defprotocol Format
  (encode [format data] "Serialize some data.")
  (decode [format data] "Unserialize some data."))