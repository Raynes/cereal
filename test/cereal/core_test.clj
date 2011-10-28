(ns cereal.core-test
  (:use clojure.test cereal.core gloss.core.formats)
  (:require (cereal.codecs [clojure  :as clj]
                           [java     :as java]
                           [protobuf :as proto]
                           [append   :as append]))
  (:import (java.nio ByteBuffer)
           (cereal Test$Foo)))

(deftest simple-codecs
  (doseq [codec [(clj/make) (java/make) (proto/make Test$Foo)]]
    (testing "decode an encoded data structure"
      (let [val {:foo 1 :bar 2}]
        (is (= val (decode codec (encode codec val))))))))

(deftest append-codecs
  (doseq [codec [(proto/make Test$Foo)]] ;; (append/make (clj/make))
      ;; [codec [ (proto/make Test$Foo) (java/make)]]
    (testing "decode an encoded data structure"
      (let [val {:foo 1 :bar 2}]
        (is (= val (decode codec (encode codec val))))))

    (testing "append two simple encoded data structures"
      (let [data1 (encode codec {:foo 1 :bar 2})
            data2 (encode codec {:foo 4 :baz 8})]
        (is (= {:foo 4 :bar 2 :baz 8}
               (decode codec (concat data1 data2))))))

    (testing "concat lists when appending"
      (let [data1 (encode codec {:tags ["foo" "bar"] :foo 1})
            data2 (encode codec {:tags ["baz" "foo"] :foo 2})]
        (is (= {:foo 2 :tags ["foo" "bar" "baz" "foo"]}
               (decode codec (concat data1 data2))))))

    (testing "merge maps when appending"
      (let [data1 (encode codec {:num-map {1 "one" 3 "three"}})
            data2 (encode codec {:num-map {2 "dos" 3 "tres"}})
            data3 (encode codec {:num-map {3 "san" 6 "roku"}})]
        (is (= {:num-map {1 "one" 2 "dos" 3 "san" 6 "roku"}}
               (decode codec (concat data1 data2 data3))))))

    (testing "merge sets when appending"
      (let [data1 (encode codec {:tag-set #{"foo" "bar"}})
            data2 (encode codec {:tag-set #{"baz" "foo"}})]
        (is (= {:tag-set #{"foo" "bar" "baz"}}
               (decode codec (concat data1 data2))))))

    (testing "support set deletion using existence map"
      (let [data1 (encode codec {:tag-set #{"foo" "bar" "baz"}})
            data2 (encode codec {:tag-set {"baz" false "foo" true "zap" true "bam" false}})]
        (is (= {:tag-set #{"foo" "bar" "zap"}}
               (decode codec (concat data1 data2))))))

    (testing "merge and append nested data structures when appending"
      (let [data1 (encode codec {:nested {:foo 1 :tags ["bar"] :nested {:tag-set #{"a" "c"}}}})
            data2 (encode codec {:nested {:foo 4 :tags ["baz"] :bar 3}})
            data3 (encode codec {:nested {:baz 5 :tags ["foo"] :nested {:tag-set {"b" true "c" false}}}})]
        (is (= {:nested {:foo 4 :bar 3 :baz 5 :tags ["bar" "baz" "foo"] :nested {:tag-set #{"a" "b"}}}}
               (decode codec (concat data1 data2 data3))))))))

(deftest codec-fields
  (comment let [codec (reader/make (with-meta {:foo 1 :bar 3}
                              {:foo {:type :int}
                               :bar {:type :string :limit 8}}))]
    (is (= {:foo {:type :int}
            :bar {:type :string :limit 8}}
           (fields codec))))
  (let [codec (proto/make Test$Foo)]
    (is (= {:foo     {:type :int},
            :bar     {:type :int},
            :baz     {:type :int},
            :tags    {:repeated true, :type :string},
            :tag-set {:repeated true, :type :message},
            :num-map {:repeated true, :type :message},
            :nested  {:type :message},
            :rev     {:type :int}}
           (fields codec)))
    (is (= {:key {:type :int},
            :val {:type :string}}
           (fields codec [:num-map])))
    (is (= {:key {:type :int},
            :val {:type :string}}
           (fields codec [:nested :nested :num-map])))))
