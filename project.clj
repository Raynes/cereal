(defproject cereal "0.2.0-beta1"
  :url "https://github.com/flatland/cereal"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :description "Revisioned, appendable Gloss codecs."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [gloss "0.2.1"]
                 [useful "0.8.3-alpha8"]
                 [io "0.2.0-beta2"]]
  :aliases {"testall" ["with-profile" "dev,default:dev,1.3,default:dev,1.5,default" "test"]}
  :profiles {:1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.0-master-SNAPSHOT"]]}}
  :repositories {"sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}})
