(ns cereal.codecs.append
  (:use [cereal.formats :only [buf-seq->stream]]
        [cereal.core :only [Fields fields]]
        [gloss.core.protocols :only [Reader Writer write-bytes sizeof]]
        [gloss.core.formats :only [to-buf-seq]]
        [gloss.core :only [finite-frame]]
        [gloss.io :only [decode-all]]
        [useful.map :only [map-to]]
        [clojure.java.io :only [reader]])
  (:import (java.io PushbackReader)))

(deftype Append [finite-codec combine-fn]
  Reader
  (read-bytes [this buf-seq]
    [true (combine-fn
           (decode-all finite-codec buf-seq)) nil])

  Writer
  (sizeof [this]
    (sizeof finite-codec))
  (write-bytes [this buf val]
    (write-bytes finite-codec buf val))

  Fields
  (fields [this]
    (fields finite-codec))
  (fields [this subfields]
    (fields finite-codec subfields)))

(defn make
  ([prefix-or-len frame combine-fn]
     (Append. (finite-frame prefix-or-len frame) combine-fn))
  ([finite-frame combine-fn]
     (Append. finite-frame combine-fn)))