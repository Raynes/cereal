(ns cereal.core-test
  (:use clojure.test
        [cereal format java]))

(def serializable (range 300))

(deftest format-test
  (let [format (make)]
    (testing "Output of encode is a byte array."
      (is (= "class [B" (->> serializable (encode format) type str))))
    
    (testing "Output of decode is the input to encode."
      (is (= serializable (->> serializable (encode format) (decode format)))))))