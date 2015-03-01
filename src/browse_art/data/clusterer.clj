(ns browse-art.data.clusterer
  (:use [clj-ml.data]
        [clj-ml.clusterers])
  (:require [browse-art.db.db :as db]))

(defn process-words
  []
  (reduce 
    (fn[acc row]
      {:words (conj (:words acc) (keyword (:word row)))
       :word-ids (conj (:word-ids acc) (:id row))}) 
    {:words [] :word-ids []} 
    (db/execute-query 
     "select count(*) c, w.id, word 
   from word_location l join word_list w on (l.word_id=w.id ) 
   group by word_id having c>5 and c<60")))

(defn create-instances
  [object-ids word-ids]
  (reduce
    (fn [instances obj-id]
      (conj 
        instances 
        (reduce
          (fn [instance word-id] 
            (conj 
              instance 
              (:count (db/count-word-occurrence word-id obj-id))))
          [obj-id]
          word-ids)))
    []
    object-ids))

(defn create-dataset
  [object-ids words-map]
  (make-dataset 
    "word_groups" 
    (vec (cons :object-id (:words words-map) ))
    (create-instances object-ids (:word-ids words-map))))

(defn make-clusters
  [object-ids]
  (let [words-map (process-words)
        ds (dataset-remove-attribute-at (create-dataset object-ids words-map) 0)
        kmeans (make-clusterer :k-means {:number-clusters 20})]
    (clusterer-build kmeans ds)
    (reduce 
      #(conj % (.value %2 (count (:words words-map))))
      []
      (clusterer-cluster kmeans ds))))

(defn add-cluster-info
  []
  (let [object-ids (reduce #(conj % (:object_id %2)) [] (db/get-all-object-ids))
        clusters (make-clusters object-ids)]
  (loop [x 0]
    (when (< x (count object-ids))
      (db/add-cluster-info (get object-ids x) (get clusters x))
      (recur (inc x))))))
