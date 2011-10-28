(ns cereal.core
  (:use [useful.macro :only [defalias]])
  (:require gloss.io))

(defalias decode gloss.io/decode)
(defalias encode gloss.io/encode)

(defprotocol Fields
  (fields [codec] [codec subfields]
    "Return a map of fields to metadata."))

(extend-type Object
  Fields
  (fields [codec] nil)
  (fields [codec subfields] nil))
