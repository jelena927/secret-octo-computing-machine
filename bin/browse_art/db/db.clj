(ns browse-art.db.db
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec {:classname "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname "browse-art.db"})

(defmacro with-db [f & body]
  '(jdbc/with-connection ~db-spec (~f ~@body)))

(defn create-db
  []
  (jdbc/with-connection db-spec))

(defn create-tables
  []
  (jdbc/with-connection db-spec
	  (jdbc/create-table :word_list
	                     [:id "integer primary key"]
	                     [:word "varchar"]))
	(jdbc/with-connection db-spec
	  (jdbc/create-table :word_location
	                     [:id "integer primary key"]
	                     [:object_id "integer"]
	                     [:word_id "integer"]
	                     [:location "integer"]))
  (jdbc/with-connection db-spec
	  (jdbc/create-table :object_list
	                     [:id "integer primary key"]
	                     [:title "varchar"]
	                     [:creator "varchar"]
	                     [:style "varchar"]
	                     [:collection "varchar"]
	                     [:period "varchar"]
	                     [:description "varchar"]
	                     [:keywords "varchar"]
	                     [:medium "varchar"]
	                     [:object_name "varchar"]
	                     [:date_begin_year "integer"]
	                     [:date_end_year "integer"]
	                     [:image "varchar"]
	                     [:object_id "integer"])))
           
(defn create-index
 []
  (jdbc/with-connection db-spec
	  (jdbc/do-commands "CREATE INDEX object_idx ON object_list (object_id)"))
	(jdbc/with-connection db-spec
	  (jdbc/do-commands "CREATE INDEX word_idx ON word_list (word)"))
	(jdbc/with-connection db-spec
	  (jdbc/do-commands "CREATE INDEX word_url_idx ON word_location (word_id)")))

(defn save-word-location 
  [{:keys [object-id word-id location]}]
  (jdbc/with-connection db-spec
    (jdbc/insert-values :word_location
      [:object_id :word_id :location]
      [object-id word-id location])))

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
    (jdbc/with-query-results res 
      [(str "select id from " table " where " field "='" (clojure.string/replace value #"'" "''") "'")];escaping apostrophe char
      (get (first res) :id))))

(defn get-and-create-entry-id
  "Getting id and adding it if not present."
  [table field value]
  (if-let [id (get-id table field value)]
    id
    (create-new-entry table field value)))
		           
(defn save-object
  [obj]
  (jdbc/with-connection db-spec
    (jdbc/insert-values :object_list 
                        [:title :creator :style :collection :period :description :keywords :medium
                         :object_name :date_begin_year :date_end_year :image :object_id] 
                        [(:Title obj) (:Creator obj) (:Style obj) (:Collection obj) (:Period obj)
                         (:Description obj) (:Keywords obj) (:Medium obj) (:ObjectName obj) (:DateBeginYear obj) 
                         (:DateEndYear obj) (first (clojure.string/split (:Images obj) #",")) (:ObjectID obj)])))

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

(defn get-all-object-images
  []
  (jdbc/with-connection db-spec
    (jdbc/with-query-results res [(str "select object_id, image from object_list")]
      (doall res))))

(defn get-object-image
  [object-id]
  (jdbc/with-connection db-spec
    (jdbc/with-query-results res [(str "select image from object_list where object_id=" object-id)]
      (get (first res) :image))))

(defmacro start-transaction
  [f]
  (jdbc/with-connection db-spec
    (jdbc/transaction
      ~f)))

