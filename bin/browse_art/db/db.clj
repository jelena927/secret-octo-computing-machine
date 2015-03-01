(ns browse-art.db.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (require [clojure.java.jdbc :as jdbc]
           [jdbc.pool.c3p0    :as pool]))

(def spec
  (pool/make-datasource-spec
    {:classname "org.sqlite.JDBC"
    :subprotocol "sqlite"
    :subname "browse-art-project.db"
    :initial-pool-size 6}))

(defn get-spec [] spec)

(defn create-tables
  []
  (jdbc/with-connection spec
	  (jdbc/create-table :word_list
                      [:id "integer primary key"]
                      [:word "varchar"])
	  (jdbc/create-table :word_location
                      [:id "integer primary key"]
                      [:object_id "integer"]
                      [:word_id "integer"]
                      [:location "integer"])
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
                      [:classification "varchar"]
                      [:object_id "integer"]
                      [:cluster "integer"])))
           
(defn create-index
  []
	(jdbc/with-connection spec
	  (jdbc/do-commands "CREATE INDEX object_idx ON object_list (object_id)")
		(jdbc/do-commands "CREATE INDEX word_idx ON word_list (word)")
	  (jdbc/do-commands "CREATE INDEX word_url_idx ON word_location (word_id)")))

(defmacro with-db [f & body]
  `(jdbc/with-connection (get-spec) (~f ~@body)))

(defn save-word-location 
  [object-id word-id location]
  (with-db 
    jdbc/insert-values :word_location
                       [:object_id :word_id :location]
                       [object-id word-id location]))

(defn create-new-entry 
  [table field value]
  (first 
    (vals 
      (with-db 
        jdbc/insert-values 
        (keyword table) [(keyword field)] [ value ]))))

(defn get-id
  [table field value]
  (with-db 
    jdbc/with-query-results res 
    [(str "select id from " table " where " field "='" (clojure.string/replace value #"'" "''") "'")];escaping apostrophe char
    (get (first res) :id)))

(defn get-and-create-entry-id
  "Getting id and adding it if not present."
  [table field value]
  (if-let [id (get-id table field value)]
    id
    (create-new-entry table field value)))
		           
(defn save-object
  [obj]
  (with-db 
    jdbc/insert-values :object_list 
    [:title :creator :style :collection :period :description :keywords :medium
     :object_name :date_begin_year :date_end_year :image :classification :object_id] 
    [(:Title obj) (:Creator obj) (:Style obj) (:Collection obj) (:Period obj)
     (:Description obj) (:Keywords obj) (:Medium obj) (:ObjectName obj) (:DateBeginYear obj) 
     (:DateEndYear obj) (first (clojure.string/split (:Images obj) #",")) 
     (:Classification obj)(:ObjectID obj)]))

(defn select-all 
  [table]
	(with-db 
   jdbc/with-query-results res [(str "select * from " table)] 
    (doall res)))

(defn get-word-id
  [word]
  (get-id "word_list" "word" word))

(defn execute-query
  [full-query]
  (with-db jdbc/with-query-results res [full-query]
    (doall res)))

(defn get-all-object-images
  []
  (with-db 
    jdbc/with-query-results res [(str "select object_id, image from object_list")]
    (doall res)))

(defn get-object-image
  [object-id]
  (with-db 
    jdbc/with-query-results res [(str "select image from object_list where object_id=" object-id)]
    (get (first res) :image)))

(defmacro start-transaction
  [& body]
  `(jdbc/with-connection (get-spec)
     (jdbc/transaction*
      (fn [] ~@body))))


(defn get-object
 [object-id]
   (with-db 
     jdbc/with-query-results res [(str "select * from object_list where object_id=" object-id)]
     (first res)))

(defn exists-table?
  []
  (execute-query "SELECT name 
                  FROM sqlite_master 
                  WHERE type='table' AND name='object_list';"))

(defn count-word-occurrence
  [word-id object-id]
  (first 
	  (execute-query 
	    (str "SELECT count(*) as count
	          FROM word_location 
	          WHERE word_id=" word-id " and object_id=" object-id))))

(defn get-all-object-ids
  []
  (with-db 
    jdbc/with-query-results res [(str "select object_id from object_list")]
    (doall res)))

(defn add-cluster-info
  [object-id cluster]
  (with-db jdbc/update-values "object_list"
     [(str "object_id=" object-id)] {:cluster cluster}))

(defn get-recommendation
  [obj-id cluster]
  (execute-query 
    (str "select object_id, image 
          from object_list 
          where cluster=" cluster " and object_id<>" obj-id)))

