(ns flatland.cereal.protobuf
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
        (loop [acc 0, shift 0, bufseq original-bufseq]
          (let [[success x bufseq] (protocol/read-bytes byte-codec bufseq)]
            (if-not success
              [false this original-bufseq]
              (if (zero? (bit-and x top-bit))
                [true (+ acc (bit-shift-left x shift)) bufseq]
                (recur (+ acc (bit-shift-left (bit-and x lower-7)
                                              shift))
                       (+ shift n-bits)
                       bufseq))))))

      ;; TODO make arithmetic primitive

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

(comment
 (def labeled-field
   (header field-label
           (fn [[field-number wire-type]]
             (case write-type
               0 varint)))))

;; this is probably a monoid?
(defprotocol Combinable
  (combine [this other])
  (default [this]) ;; probably can be shoved into the value function
  (value [this]))

(defn entries [field-formats]
  (reify Reader
    (read-bytes [this b]
      (let [xs (read-bytes frame b)]
        (reify
          Combinable
          (combine [this other]
            ...)
          (value [this]
            xs)
          (default [this]
            (use field formats to build aggregated default)))))))

(comment thinking about making it look like this
         (proto/message {1 {:key :name, :format (proto/string)}
                         2 {:key :age, :format (proto/varint)}
                         3 {:key :jobs
                            :pre-process vector, :combine into
                            :format (proto/message {1 {:key :company,
                                                       :format (proto/string)}
                                                    2 {:key :years-worked,
                                                       :format (proto/varint)}})
                            :post-process (proto/map-by :company)}})

         ;; what about stopping early?
         ;; folded doesn't actually make the field fold, but decorates the values with
         ;; a protocol (Combinable) that knows how to combine itself on demand.
         ;; then we can take-while over that, and the proto/message wrapper is only
         ;; responsible for making the map-entries into a struct at the end
         (proto/message
          (proto/take-while (fn ...)
                            (proto/entries {1 (-> (proto/field {:key :name :format (proto/string)})
                                                  (proto/folded ...))
                                            ...})))

         (proto/message {1 (proto/field {:key :name, :format (proto/string)})
                         2 (proto/field {:key :age, :format (proto/varint)})
                         3 (let [map-format (proto/message
                                             {1 (proto/field {:key :company,
                                                              :format (proto/string)})
                                              2 (proto/field {:key :years-worked,
                                                              :format (proto/varint)})})]
                             (-> (proto/field {:key :jobs :format map-format})
                                 (proto/folded {:init {}, :fold (proto/map-by :company map-format)
                                                :unfold seq})))})

         (-> (proto/message {:name {:key 1, :format (proto/string)}
                             :age {:key 2, :format (proto/varint)}
                             :jobs {:key 3
                                    :pre-process vector, :combine into
                                    :format (proto/message {1 {:key :company,
                                                               :format (proto/string)}
                                                            2 {:key :years-worked,
                                                               :format (proto/varint)}})
                                    :post-process (proto/map-by :company)}})
             (wrap-munge-field :jobs {:fold ... :unfold ...})))
