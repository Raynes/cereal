(ns cereal.core-test
  (:use clojure.test cereal.core gloss.io)
  (:import (java.nio ByteBuffer)))

(deftest simple-codecs
  (doseq [codec [(clojure-codec) (java-codec)]]
    (testing "decode an encoded data structure"
      (let [val {:foo 1 :bar 2}]
        (is (= val (decode codec (encode codec val)))))))

  (doseq [codec [(clojure-codec :validator map?) (java-codec :validator map?)]]
    (testing "validators"
      (is (thrown-with-msg? IllegalStateException #"Invalid"
            (encode codec [1 2 3 4]))))))
