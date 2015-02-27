(ns browse-art.db.mongo
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all])
  (:import org.bson.types.ObjectId))

(let [conn (mg/connect {:host "127.0.0.1" :port 27017})
      db   (mg/get-db conn "browse-art-db")]
  
  (defn return-db [] db)
  
  (defn create-tables []
    (mc/create db "object_list" {})
    (mc/create db "word_list" {})
    (mc/create db "word_location" {}))
  
  (defn create-index []
    (mc/ensure-index db "object_list" {:object_id 1} { :name "object_idx" })
    (mc/ensure-index db "word_list" {:word 1} { :name "word_idx" })
    (mc/ensure-index db "word_location" {:word_id 1} { :name "location_idx" }))
  
  (defn save-word-location 
	  [[object-id word-id location]]
	  (mc/insert 
	    db 
	    "word_location"
	    {:_id (ObjectId.) :object_id object-id :word_id word-id :location location}))
  
  (defn create-new-entry 
	  [table field value]
	  (:_id (mc/insert-and-return db table {(keyword field) value })))
  
  (defn get-id
	  [table field value]
		(:_id (mc/find-one-as-map db table {(keyword field) value})))
  
  (defn get-and-create-entry-id
  "Getting id and adding it if not present."
	  [table field value]
	  (if-let [id (get-id table field value)]
	    id
	    (create-new-entry table field value)))
		           
	(defn save-object
	  [obj]
	  (mc/insert 
      db
      "object_list" 
      {:title (:Title obj) :creator (:Creator obj) :style (:Style obj) 
       :collection (:Collection obj) :period (:Period obj) :description (:Description obj)
       :keywords (:Keywords obj) :medium (:Medium obj) :object_name (:ObjectName obj)
       :date_begin_year (:DateBeginYear obj) :date_end_year (:DateEndYear obj) 
       :image (first (clojure.string/split (:Images obj) #",")) :object_id (:ObjectID obj)}))

	(defn select-all 
	  [table]
		(mc/find-maps db table))

	(defn get-word-id
	  [word]
	  (get-id "word_list" "word" word))
	
;	(defn execute-query
;	  [full-query]
;	  (jdbc/with-connection db-spec
;	    (jdbc/with-query-results res [full-query]
;	      (doall res))))
	
;{:rows 
; ({:location_3 "9", :location_2 "13", 
;   :location "0", :object_id "19"}), 
; :word-ids [1 1160 1156]}

	(defn get-all-object-images
	  []
	  (mc/find-maps db "object_list" {} {:object_id 1 :image 2}))
	
	(defn get-object-image
	  [object-id]
	  (:image (mc/find-one-as-map db "object_list" {:object_id object-id})))
 
	 (defn get-object
	   [object-id]
	   (mc/find-one-as-map db "object_list" {:object_id object-id})))
  
  
  
  