(ns flatland.cereal.core-test
  (:use clojure.test flatland.cereal.core gloss.io
        [gloss.core :only [repeated compile-frame finite-frame]]
        [flatland.useful.utils :only [adjoin]])
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

(defn- adjoining [codec]
  (compile-frame codec list (partial reduce adjoin)))

(deftest appending
  (doseq [codec [(adjoining (clojure-codec :repeated true))
                 (adjoining (java-codec    :repeated true))]]
    (testing "append two simple encoded data structures"
      (let [data1 (encode codec {:foo 1 :bar 2})
            data2 (encode codec {:foo 4 :baz 8 :bap [1 2 3]})
            data3 (encode codec {:foo 3 :bap [10 11 12]})]
        (is (= {:foo 3 :bar 2 :baz 8 :bap [1 2 3 10 11 12]}
               (decode codec (concat data1 data2 data3))))))))