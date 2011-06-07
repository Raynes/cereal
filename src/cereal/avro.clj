(ns cereal.avro
  "Avro backend."
  (:use cereal.format
        simple-avro.core))

(deftype AvroFormat [schema encoder decoder]
  Format
  (encode [format data]
    (pack schema data encoder))
  (decode [format data]
    (unpack schema data decoder)))

(defn make
  "Create an instance of AvroFormat that uses schema. Optionally takes
   a decoder and encoder."
  [schema & [encoder decoder]]
  (AvroFormat. schema
               (or encoder binary-encoder)
               (or decoder binary-decoder)))