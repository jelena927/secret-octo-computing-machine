(ns browse-art.core
  (:require [browse-art.db :as db]))

(defn db-initial-setup
  []
  (do
    (db/create-db)
    (db/create-tables)
    (db/create-index)))

;(defn start 
;  []
;  (crawler/crawl)
;  (run-jetty application {:port port :join? false}))
;
;(defn -main 
;  []
;  (start))