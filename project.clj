(defproject cereal "0.1.1"
  :description "Dead simple serialization in Clojure."
  :dependencies [[clojure "1.2.0"]
                 [clojure-useful "0.3.8"]]
  :dev-dependencies [[clojure-protobuf "0.4.0"]]
  :tasks [protobuf.tasks])
