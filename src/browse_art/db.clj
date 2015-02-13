(ns browse-art.db
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec {:classname "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname "browse-art.db"})

(defn create-db
  []
  (jdbc/with-connection db-spec))

(defn create-tables
  []
  (do
		(jdbc/with-connection db-spec
		  (jdbc/create-table :url_list
		                     [:id "integer primary key"]
		                     [:url "varchar"]))
		(jdbc/with-connection db-spec
		  (jdbc/create-table :word_list
		                     [:id "integer primary key"]
		                     [:word "varchar"]))
		(jdbc/with-connection db-spec
		  (jdbc/create-table :word_location
		                     [:id "integer primary key"]
		                     [:url_id "varchar"]
		                     [:word_id "varchar"]
		                     [:location "varchar"]))
		(jdbc/with-connection db-spec
		  (jdbc/create-table :link
		                     [:id "integer primary key"]
		                     [:from_id "integer"]
		                     [:to_id "integer"]))
		(jdbc/with-connection db-spec
		  (jdbc/create-table :link_words
		                     [:id "integer primary key"]
		                     [:word_id "integer"]
		                     [:link_id "integer"]))))

(defn create-index
  []
  (do
    (jdbc/with-connection db-spec
		  (jdbc/do-commands "CREATE INDEX url_idx ON url_list (url)"))
		(jdbc/with-connection db-spec
		  (jdbc/do-commands "CREATE INDEX word_idx ON word_list (word)"))
		(jdbc/with-connection db-spec
		  (jdbc/do-commands "CREATE INDEX word_url_idx ON word_location (word_id)"))
		(jdbc/with-connection db-spec
		  (jdbc/do-commands "CREATE INDEX url_to_idx ON link (to_id)"))
		(jdbc/with-connection db-spec
		  (jdbc/do-commands "CREATE INDEX url_from_idx ON link (from_id)"))))

(defn save-word-location 
  [{:keys [url-id word-id location]}]
  (jdbc/with-connection db-spec
    (jdbc/insert-values :word_location
      [:url_id :word_id :location]
      [url-id word-id location])))

;(defn save-record 
;  [table record-map]
;  (first 
;    (vals 
;		  (jdbc/with-connection db-spec
;		    (jdbc/insert-record (keyword table) record-map)))))

(defn create-new-entry 
  [table field value]
  (first 
    (vals 
      (jdbc/with-connection db-spec
        (jdbc/insert-values (keyword table) [(keyword field)] [ value ])))))
  
;(defn select-last-id [table]
;	(jdbc/with-connection db-spec
;	  (jdbc/with-query-results res [(str "SELECT MAX(id) as last FROM " table)] 
;	    (get (first res) :last))))

;(defn generate-id [table]
;  (do
;    (jdbc/with-connection db-spec
;     (jdbc/do-commands 
;         (str "insert into " table " DEFAULT VALUES ")))
;    (select-last-id table)))

(defn get-id
  [table field value]
  (jdbc/with-connection db-spec
    (jdbc/with-query-results res [(str "select id from " table " where " field "='" value "'")]
      (get (first res) :id))))

(defn get-and-create-entry-id
  "Getting id and adding it if not present."
  [table field value]
  (if-let [id (get-id table field value)]
    id
    (create-new-entry table field value)))

(defn select-all 
  [table]
	(jdbc/with-connection db-spec
	  (jdbc/with-query-results res [(str "select * from " table)] 
	    (doall res))))

(defn get-word-id
  [word]
  (get-id "word_list" "word" word))

(defn execute-query
  [full-query]
  (jdbc/with-connection db-spec
    (jdbc/with-query-results res [full-query]
      (doall res))))

(defn get-url-name
  [url-id]
  (jdbc/with-connection db-spec
    (jdbc/with-query-results res [(str "select url from url_list where id='" url-id "'")]
      (get (first res) :url))))

;ne treba za sad
(defn db-commit
  []
  )
