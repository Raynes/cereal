(ns cereal.core-test
  (:use clojure.test cereal.format)
  (:require [cereal.java :as java]
            [cereal.reader :as reader]
            [cereal.protobuf :as proto])
  (:import cereal.Test$Foo))

(defn catbytes [& args]
  (.getBytes (apply str (map #(String. %) args))))

(deftest cereal-test
  (doseq [format [(reader/make) (proto/make Test$Foo) (java/make)]]
    (testing "decode an encoded data structure"
      (let [val {:foo 1 :bar 2}]
        (is (= val (decode format (encode format val))))))

    (testing "append two simple encoded data structures"
      (let [data1 (encode format {:foo 1 :bar 2})
            data2 (encode format {:foo 4 :baz 8})]
        (is (= {:foo 4 :bar 2 :baz 8}
               (decode format (catbytes data1 data2))))))

    (testing "concat lists when appending"
      (let [data1 (encode format {:tags ["foo" "bar"] :foo 1})
            data2 (encode format {:tags ["baz" "foo"] :foo 2})]
        (is (= {:foo 2 :tags ["foo" "bar" "baz" "foo"]}
               (decode format (catbytes data1 data2))))))

    (testing "merge maps when appending"
      (let [data1 (encode format {:num-map {1 "one" 3 "three"}})
            data2 (encode format {:num-map {2 "dos" 3 "tres"}})
            data3 (encode format {:num-map {3 "san" 6 "roku"}})]
        (is (= {:num-map {1 "one" 2 "dos" 3 "san" 6 "roku"}}
               (decode format (catbytes data1 data2 data3))))))

    (testing "merge sets when appending"
      (let [data1 (encode format {:tag-set #{"foo" "bar"}})
            data2 (encode format {:tag-set #{"baz" "foo"}})]
        (is (= {:tag-set #{"foo" "bar" "baz"}}
               (decode format (catbytes data1 data2))))))

    (testing "support set deletion using existence map"
      (let [data1 (encode format {:tag-set #{"foo" "bar" "baz"}})
            data2 (encode format {:tag-set {"baz" false "foo" true "zap" true "bam" false}})]
        (is (= {:tag-set #{"foo" "bar" "zap"}}
               (decode format (catbytes data1 data2))))))

    (testing "merge and append nested data structures when appending"
      (let [data1 (encode format {:nested {:foo 1 :tags ["bar"] :nested {:tag-set #{"a" "c"}}}})
            data2 (encode format {:nested {:foo 4 :tags ["baz"] :bar 3}})
            data3 (encode format {:nested {:baz 5 :tags ["foo"] :nested {:tag-set {"b" true "c" false}}}})]
        (is (= {:nested {:foo 4 :bar 3 :baz 5 :tags ["bar" "baz" "foo"] :nested {:tag-set #{"a" "b"}}}}
               (decode format (catbytes data1 data2 data3))))))))

(deftest test-fields
  (let [format (reader/make (with-meta {:foo 1 :bar 3}
                              {:foo {:type :int}
                               :bar {:type :string :limit 8}}))]
    (is (= {:foo {:type :int}
            :bar {:type :string :limit 8}}
           (fields format))))
  (let [format (proto/make Test$Foo)]
    (is (= {:foo     {:repeated false, :type :int},
            :bar     {:repeated false, :type :int},
            :baz     {:repeated false, :type :int},
            :tags    {:repeated true, :type :string},
            :tag-set {:repeated true, :type :message},
            :num-map {:repeated true, :type :message},
            :nested  {:repeated false, :type :message},
            :rev     {:repeated false, :type :int}}
           (fields format)))))
