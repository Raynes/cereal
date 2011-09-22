(ns cereal.format)

(defprotocol Format
  (encode        [format data]                          "Serialize some data.")
  (decode        [format data] [format data offset len] "Unserialize some data.")
  (decode-stream [format stream]                        "Unserialize a stream." )
  (fields        [format] [format [subfields]]          "Return a map of fields to metadata."))