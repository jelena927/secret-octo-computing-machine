(ns browse-art.data.data-loader
  (:require [clojure.data.json :as json]))
  
(def get_data_url (str "http://api.thewalters.org/v1/objects.json?apikey=4vyusmfXspfNUhVaDufgl8hxeGmoTWxZtLBL5AlGZF12KkVizFhFcbFwD0Gc0r3L"
                        "&page=" 10 "&pageSize=" 100))

(defn my-value-reader [key value]
  (if (= key :date)
    (java.sql.Date/valueOf value)
    value))

(defn import_data 
  []
  (println "Importing data")
  (:Items (json/read (clojure.java.io/reader get_data_url)
                     :value-fn my-value-reader
                     :key-fn keyword)))