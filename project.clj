(defproject cereal "0.1.3"
  :description "Dead simple serialization in Clojure."
  :dependencies [[clojure "1.2.0"]
                 [useful "0.4.0"]
                 [gloss "0.2.0-alpha1"]]
  :dev-dependencies [[clojure-protobuf "0.4.4"]]
  :tasks [protobuf.tasks])
