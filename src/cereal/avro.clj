(ns cereal.avro
  "Avro backend."
  (:use [cereal format netstring]
        [useful :only [adjoin]]
        simple-avro.core
        [clojure.walk :only [stringify-keys]]))

(deftype AvroFormat [schema]
  Format
  
  (encode [format data]
    (encode-netstring (String. (pack schema (stringify-keys data) binary-encoder))))
  
  (decode [format data]
    (apply merge-with adjoin
           (map (comp #(unpack schema (.getBytes (str %)) binary-decoder))
                (decode-netstring data)))))

(defn make
  "Create an instance of AvroFormat that uses schema. Optionally takes
   a decoder and encoder."
  [schema]
  (AvroFormat. schema))