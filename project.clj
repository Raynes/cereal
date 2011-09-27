(defproject cereal "0.1.7"
  :description "Dead simple serialization in Clojure."
  :dependencies [[clojure "1.2.0"]
                 [useful "0.7.0-beta4"]]
  :dev-dependencies [[protobuf "0.5.0-alpha4"]
                     [gloss "0.2.0-alpha1"]
;;                   [cake-marginalia "0.6.0"]
                     [clojure-contrib "1.2.0"]]
;;  :tasks [cake-marginalia.tasks]
  :cake-plugins [[cake-protobuf "0.5.0-alpha5"]])
