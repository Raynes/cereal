(defproject cereal "0.1.5"
  :description "Dead simple serialization in Clojure."
  :dependencies [[clojure "1.2.0"]
                 [useful "0.5.0"]]
  :dev-dependencies [[protobuf "0.5.0-alpha1"]
                     [gloss "0.2.0-alpha1"]
                     [clojure-contrib "1.2.0"]]
  :tasks [protobuf.tasks])
