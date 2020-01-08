(defproject clj-ocgeo "0.2.0"
  :description "A Clojure library for accessing the OpenCage Geocoding API"
  :url "https://github.com/sgsfak/clj-ocgeo"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "0.2.7"]
                 [clj-http "3.10.0"]]
  :repl-options {:init-ns clj-ocgeo.core})
