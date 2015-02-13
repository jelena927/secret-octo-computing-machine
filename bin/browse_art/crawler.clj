(ns browse-art.crawler
  (:use [jsoup.soup])
  (:require [browse-art.db :as db]))

(def ignore-words ["the", "of", "to", "and", "a", "in", "is", "it"])

(def page-url ["http://www.google.rs"])
(def pages-vector ["asd" "http://www.google.rs" "http://en.wikipedia.org/wiki/Saint-Maurice,_Val-de-Marne"])

(defn get-links 
  [page]
  (try
	  (vec ($ (get! page) 
       "a[href]" 
      (attr "abs:href")))
	  (catch Exception e [])))

(defn get-html 
  [page]
  (try
	  ($ (get! page)
      "html")
	  (catch Exception e ())))

(defn get-text-only
  [html]
  (try
	  (apply str ($ html (text)))
	  (catch Exception e ())))

(defn create-index-tables
  [])

(defn separate-words
  [text]
  (re-seq #"\b[^\s]+\b" text))

(defn indexed?
  [url]
  (if-let [url-id (db/get-id "url_list" "url" url)]
    (if (db/get-id "word_location" "url_id" url-id)
      true
      false)
    false))

(defn add-to-index ;ne radi za d'entreprise
  [url html]
  (if-not (indexed? url)
   (let [url-id (db/get-and-create-entry-id "url_list" "url" url)]
	   (reduce 
	     (fn [counter word]
		     (if-not (some #(= word %) ignore-words)
		       (do 
             (db/save-word-location {:url-id url-id 
		                                   :word-id (db/get-and-create-entry-id "word_list" "word" word) 
		                                   :location counter})
             (inc counter))))
       0
	    (separate-words (get-text-only html))))))

(defn add-link-ref
  "Add a link between two pages"
  [url-from url-to link-text]
  )

(defn crawl-final
  ([pages] (crawl-final pages 2))
  ([pages depth]
  (if (> depth 0)
    (recur 
	     (reduce 
	       (fn [new-pages one-page]
           (do
             (add-to-index one-page (get-html one-page))
	           (into new-pages 
	             (reduce 
			           (fn [links link]
			             (if (and  (= (.indexOf link "http") 0) (not (indexed? link))) 
			                (do 
	                     (add-link-ref one-page link (get-text-only link))
	                     (conj links (get (clojure.string/split link #"#") 0))))) 
					        []
					        (get-links one-page)))))
	       []
	       pages)
		    (- depth 1)))))



;(map #(crawl % 3) pages-vector)
; ((fn [x] (+ x 1)) 2)

;;debugging parts of expressions
(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))




