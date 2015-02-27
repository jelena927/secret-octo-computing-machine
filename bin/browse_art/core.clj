(ns browse-art.core
  (:gen-class :main true)
  (:require [browse-art.db.db :as db]
            [browse-art.view.controller :as controller]
            [browse-art.data.data-loader :as loader]
            [browse-art.data.indexer :as indexer])
  (:use [ring.adapter.jetty :only (run-jetty)]
			  [compojure.core :only (GET POST PUT defroutes)]
			  [compojure.handler :only (api)]
			  [compojure.route :as route]))

(defn db-initial-setup
  []
  (do
    (db/create-tables)
    (db/create-index)))

(defroutes app*
  (GET "/" [] (controller/search-result ""))
  (GET "/search" [query] (controller/search-result query))
  (GET "/object/:id" [id] (controller/show id))
  (route/resources "/")
  (route/not-found "Page not found!"))

(def app (api app*))

(defn start 
  []
  (run-jetty app {:port 8080 :join? false}))
;(Thread/sleep 2000)
(defn -main 
  []
  (println 
    "app start up"
;    (indexer/add-to-index (loader/import_data))
    "proso indexer")
  (start)
  )