(ns cereal.java
  (:use cereal.format)
  (:import (java.io ByteArrayInputStream ByteArrayOutputStream
                    ObjectInputStream ObjectOutputStream)))

(deftype JavaFormat []
  Format
  
  (encode [format data]
    (let [byte-stream (ByteArrayOutputStream.)]
      (.writeObject (ObjectOutputStream. byte-stream) data)
      (.toByteArray byte-stream)))

  (decode [format data]
    (.readObject (ObjectInputStream. (ByteArrayInputStream. data)))))

(defn make [] (JavaFormat.))