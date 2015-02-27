(ns browse-art.data.indexer
  (:require [browse-art.db.db :as db]))

(def ignore-words ["the", "of", "to", "and", "a", "in", "is", "it"])

(defn extract-text
  [object]
  (str (:Title object) " " (:Creator object) " " (:Description object) " " 
       (:Keywords object) " " (:Style object) " " (:Collection object) " " 
       (:Period object) " " (:Medium object) " " (:ObjectName object)))

(defn separate-words
  [text]
  (re-seq #"\b[^\s]+\b" text))

(defn indexed?
  [object-id]
  (if (db/get-id "object_list" "object_id" object-id)
    (if (db/get-id "word_location" "object_id" object-id)
      true
      false)
    false))

(defn add-to-index 
  [objects-vector]
  (db/start-transaction
	  (map
	    (fn [object]
	      (when-not (indexed? (:ObjectID object))
	        (db/save-object object)
	        (reduce 
	          (fn [counter word]
	            (if-not (some #(.equalsIgnoreCase word %) ignore-words)
	              (db/save-word-location (:ObjectID object) 
	                                      (db/get-and-create-entry-id "word_list" "word" (.toLowerCase word)) 
	                                      counter))
	            (inc counter))
	          0
	          (separate-words (extract-text object)))))
	    objects-vector)))
