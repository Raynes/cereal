(defproject cereal "0.1.5-SNAPSHOT"
  :description "Dead simple serialization in Clojure."
  :dependencies [[clojure "1.2.0"]
                 [useful "0.4.0"]]
  :dev-dependencies [[clojure-protobuf "0.4.6"]
                     [gloss "0.2.0-alpha1"]]
  :tasks [protobuf.tasks])
