(defproject browse-art "0.1.0-SNAPSHOT"
  :description "Clojure web application for searcing and ranking art works."
  :url "https://github.com/jelena927/secret-octo-computing-machine"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-soup/clojure-soup "0.1.1"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [sqlitejdbc "0.5.6"]
                 [org.clojure/data.json "0.2.5"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring "1.2.1"]
                 [cc.artifice/clj-ml "0.5.1"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.1"]]
  :aot  [browse-art.core]
  :main browse-art.core)
