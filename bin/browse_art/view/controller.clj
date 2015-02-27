(ns browse-art.view.controller
  (:require [browse-art.view.search-template :as search-template]
            [browse-art.view.object-template :as object-template]
            [browse-art.search.search-engine :as search-engine]
            [browse-art.db.db :as db]))


(defn search-result
  [query]
  "Search result page"
  (if (empty? query)
    (search-template/layout query 
      (reduce 
	      (fn[acc ob]
	        (conj acc [(:object_id ob) (:image ob)]))
	      []                      
        (db/get-all-object-images)))
    (search-template/layout query 
      (reduce 
	      (fn[acc [key score image]]
	        (conj acc [(name key) image]))
	      []
	      (search-engine/query query)))))

(defn show
  [id]
  "Object details page"
  (if-let [object (db/get-object (Integer. id))]
    (object-template/layout object)
    (str "Object not found!")))