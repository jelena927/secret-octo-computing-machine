(ns browse-art.core
  (:require [browse-art.db.db :as db]
            [browse-art.view.controller :as controller])
  (:use [ring.adapter.jetty :only (run-jetty)]
			  [compojure.core :only (GET POST PUT defroutes)]
			  [compojure.handler :only (api)]
			  [compojure.route :as route]))

(defn db-initial-setup
  []
  (do
    (db/create-db)
    (db/create-tables)
    (db/create-index)))

(defroutes app*
  (GET "/" [] (controller/search-result ""))
  (GET "/search" [query] (controller/search-result query))
  (GET "/object/:id" [id] {:status 200
 :headers {"Content-Type" "text/html; charset=utf-8"}
 :body "<h1>Hello user 1</h1>"});(controller/show id))
  (route/resources "/")
  (route/not-found "Page not found!"))

(def app (api app*))

(def server (run-jetty #'app {:port 8080 :join? false}))

;(defn start 
;  []
;  (crawler/crawl)
;  (run-jetty application {:port 8080 :join? false}))
;
;(defn -main 
;  []
;  (start))