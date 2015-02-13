(ns browse-art.search-engine
  (:require [browse-art.db :as db]))

(defn get-match-rows ;case sensitive je za sad ; ako ne nadje sve reci na strani ne vraca je 
  [query-words]
  (if-let 
    [query-parts 
     (reduce 
       (fn[acc word]
         (if-let [word-id (db/get-word-id word)]
           (let [table-number (:table-number acc)]
           {:field-list (str (:field-list acc) ", " (format " w%s.location " table-number))
           :table-list (str (:table-list acc) (if (pos? table-number) ", ") (format "word_location w%s " table-number))
           :clause-list 
           (str 
             (:clause-list acc) (if (pos? table-number)(format " and w%s.url_id=w%s.url_id and " (dec table-number) table-number)) 
                (format "w%s.word_id=%s " table-number word-id))
           :table-number (inc table-number)
           :word-ids (conj (:word-ids acc) word-id)})))
         {:field-list "w0.url_id" :table-list "" :clause-list "" :table-number 0 :word-ids []}
         (clojure.string/split query-words #" "))]
    {:rows (db/execute-query 
            (str "select " (:field-list query-parts) 
                " from " (:table-list query-parts)
                " where " (:clause-list query-parts)))
     :word-ids (:word-ids query-parts)}))  
;primer rezultata {:rows ({:location_3 "9", :location_2 "13", :location "0", :url_id "19"}), :word-ids [1 1160 1156]}


(defn normalize-scores
  [scores, small-better]
  (let [v-small 0.00001] ;aviod division by zero
    (if small-better
      (let [min-score (apply min (vals scores))]
        (reduce #(assoc % (key %2) (/ (float min-score) (max v-small (val %2)))) {} scores))
      (let [max-score (apply max (vals scores))] 
        (reduce #(assoc % (key %2) (/ (float (val %2)) (if (zero? max-score) v-small max-score))) {} scores)))))

(defn frequecy-score
  [rows]
  (let 
    [counts 
     (reduce 
       #(let [current-key (keyword (:url_id %2))] 
          (assoc % current-key 
            (if (contains? % current-key) 
              (inc (current-key %)) 
              1))) 
       {} 
       rows)]
     (normalize-scores counts false))) 

(defn location-score
  [rows]
  (let [locations 
    (reduce 
      (fn[acc row]
        (let [current-key (keyword (:url_id row))
              sum (reduce #(+ (Integer/parseInt %2) %) 0 (pop (vec (vals row))))] 
         (assoc acc current-key 
           (if 
              (and 
                (contains? acc current-key) 
                (< (current-key acc) sum))
             (current-key acc) 
             sum)))) 
          {} 
          rows)]
     (normalize-scores locations true)))

(defn distance-score
  [rows]
  (if (= 2 (count (first rows)))
    (reduce #(assoc % (keyword (:url_id %2)) 1.0) {} rows)
    (let 
      [min-distances 
       (reduce
         (fn [acc row]
           (let [locations (vals row)
                 current-key (keyword (:url_id row))]
	           (assoc acc current-key
		           (loop [x 1
		                  y (- (count row) 1)
                      dist 0]
	              (if (< x y)
							    (recur (+ x 1) y (+ dist (java.lang.Math/abs (- (read-string (nth locations x)) (read-string (nth locations (- x 1)))))))
                  (if 
                    (and 
                      (contains? acc current-key)
                      (> (current-key acc) dist))
                    (current-key acc)
                     dist))))))
         {}
         rows)]
      (normalize-scores min-distances true))))

(defn get-scored-list
  [rows, word-ids];({:location "20", :location2 "17", :url_id "3"} {:location "12", :location2 "7", :url_id "1"})
   (let [
         weights [[1.0 (frequecy-score rows)];{:3 1.0, :1 1.0}
                  [1.5 (location-score rows)];{:3 0.5135135135135135, :1 1.0}
                  [2.0 (distance-score rows)]]];{:3 1.0, :1 0.6}
     (reduce 
       (fn [acc [weight scores]]
         (reduce 
          (fn [total-scores [key val]]
            (assoc total-scores 
                   key 
                   (if (key total-scores)
                     (+ (key total-scores) (* weight val))
                     (* weight val ))))
          acc 
          scores)) 
       {} 
       weights)))

(defn get-url-name
  [id]
  (db/get-url-name id))

(defn query
  [q]
  (if-let [get-match-rows (get-match-rows q)]
    (let [scores (get-scored-list (get get-match-rows :rows) (get get-match-rows :word-ids))]
      (map (fn [[k v]] (println v (get-url-name (name k)))) (sort-by val > scores)))))




