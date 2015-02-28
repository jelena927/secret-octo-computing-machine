(ns browse-art.data.indexer
  (:require [browse-art.db.db :as db]))

(def ignore-words ["the", "of", "to", "and", "a", "in", "is", "it"
                   "on" "at" "too" "are" "was" "did" "not" "an" 
                   "were" "no"])

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

(defn ignore-word?
  [word]
  (some #(.equalsIgnoreCase word %) ignore-words))

(defn contains-number?
  [word]
  (if (re-find #"\d" word)
    true
    false))

(defn add-to-index 
  [objects-vector]
  (db/start-transaction
	  (map
	    (fn [object]
	      (when-not (indexed? (:ObjectID object))
         (println "Indexing" (:ObjectID object))
	        (db/save-object object)
	        (reduce 
	          (fn [counter word]
	            (if-not (or (ignore-word? word) (contains-number? word))
	              (db/save-word-location (:ObjectID object) 
	                                      (db/get-and-create-entry-id "word_list" "word" (.toLowerCase word)) 
	                                      counter))
	            (inc counter))
	          0
	          (separate-words (extract-text object)))))
	    objects-vector)))
