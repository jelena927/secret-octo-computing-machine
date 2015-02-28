(ns browse-art.core
  (:gen-class :main true)
  (:require [browse-art.db.db :as db]
            [browse-art.view.controller :as controller]
            [browse-art.data.data-loader :as loader]
            [browse-art.data.indexer :as indexer]
            [browse-art.data.clusterer :as clusterer])
  (:use [ring.adapter.jetty :only (run-jetty)]
			  [compojure.core :only (GET POST PUT defroutes)]
			  [compojure.handler :only (api)]
			  [compojure.route :as route]))

(defn db-initial-setup
  []
  (db/create-tables)
  (db/create-index))

(defroutes app*
  (GET "/" [] (controller/search-result ""))
  (GET "/search" [query] (controller/search-result query))
  (GET "/object/:id" [id] (controller/show id))
  (route/resources "/")
  (route/not-found "Page not found!"))

(def app (api app*))

(defn start-server 
  []
  (run-jetty app {:port 8080 :join? false}))

(defn -main 
  []
  (println "Starting application")
  (if-not (db/exists-table?)
    (db-initial-setup))
  (println "Starting indexer")
  (println (indexer/add-to-index (loader/import_data)))
  (println "Indexing done")
  (println "Creating clusters")
  (clusterer/add-cluster-info)
  (println "Clustering done")
  (start-server)
  )