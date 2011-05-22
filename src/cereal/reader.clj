(ns cereal.reader
  (:use [useful :only [adjoin]]
        cereal.format))

(defn- read-seq []
  (lazy-seq
   (let [form (read *in* false ::EOF)]
     (when-not (= ::EOF form)
       (cons form (read-seq))))))

(defn- read-append [defaults str]
  (with-in-str str
    (apply merge-with adjoin defaults (read-seq))))

(deftype ReaderFormat [defaults]
  Format

  (encode [format node]
    (.getBytes (pr-str node)))

  (decode [format data]
    (if data
      (read-append defaults (String. data))
      defaults))

  (decode [format data offset len]
    (if data
      (read-append defaults (String. data offset len))
      defaults))

  (fields [format]
    (keys defaults)))

(defn make [& [defaults]]
  (ReaderFormat. defaults))