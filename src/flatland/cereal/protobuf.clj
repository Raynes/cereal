(ns cereal.core
  (:require [gloss.core :as gloss]
            [gloss.core.protocols :as protocol]
            [gloss.data.bytes :as bytes]
            [gloss.io :as io])
  (:import java.nio.ByteBuffer))

(let [byte-codec (gloss/compile-frame :byte)
      top-bit (unchecked-byte 0x80)
      lower-7 (unchecked-byte 0x7f)
      n-bits 7]
  (def varint
    (reify
      protocol/Reader
      (read-bytes [this original-bufseq]
        (loop [acc 0, mult 1, bufseq original-bufseq]
          (let [[success x bufseq] (protocol/read-bytes byte-codec bufseq)]
            (if-not success
              [false this original-bufseq]
              (if (zero? (bit-and x top-bit))
                [true (+ acc (* mult x)) bufseq]
                (recur (+ acc (* mult
                                 (bit-and x lower-7)))
                       (bit-shift-left mult n-bits)
                       bufseq))))))

      protocol/Writer
      (sizeof [this] nil)
      (write-bytes [this b n]
        (let [num-continued-chunks (-> (Long/highestOneBit n)
                                       (Long/numberOfTrailingZeros)
                                       (quot 7))
              target (byte-array (inc num-continued-chunks))]
          (loop [i 0, n n]
            (if (= i num-continued-chunks)
              (aset target i (byte n))
              (do (aset target i (byte (bit-or top-bit
                                               (bit-and n lower-7))))
                  (recur (inc i) (bit-shift-right n 7)))))
          [(ByteBuffer/wrap target)])))))

(def field-label
  (gloss/compile-frame varint
                       (fn [[field-number wire-type]]
                         (bit-or (bit-shift-left field-number 3)
                                 wire-type))
                       (fn [encoded-value]
                         [(bit-shift-right encoded-value 3)
                          (bit-and encoded-value 0x7)])))
