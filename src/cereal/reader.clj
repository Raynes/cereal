(ns cereal.reader
  (:use [useful.utils :only [adjoin]]
        [useful.map :only [map-to]]
        [clojure.java.io :only [reader]]
        cereal.format)
  (:import (clojure.lang LineNumberingPushbackReader)))

(defn- read-seq []
  (lazy-seq
   (let [form (read *in* false ::EOF)]
     (when-not (= ::EOF form)
       (cons form (read-seq))))))

(defn- read-append [defaults s]
  (with-open [r (LineNumberingPushbackReader. (reader s))]
    (binding [*in* r]
      (apply merge-with adjoin defaults (read-seq)))))

(deftype ReaderFormat [defaults]
  Format

  (encode [format node]
    (.getBytes (pr-str node)))

  (decode [format data]
    (if data
      (read-append defaults data)
      defaults))

  (decode [format data offset len]
    (if data
      (read-append defaults (String. data offset len))
      defaults))

  (decode-stream [format stream]
    (if stream
      (read-append defaults stream)
      defaults))

  (fields [format]
    (map-to (or (meta defaults) {})
            (keys defaults))))

(defn make [& [defaults]]
  (ReaderFormat. defaults))