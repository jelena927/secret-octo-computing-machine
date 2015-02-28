(ns browse-art.data.data-loader
  (:require [clojure.data.json :as json]))
  
(def get_data_url (str "http://api.thewalters.org/v1/objects.json?apikey=4vyusmfXspfNUhVaDufgl8hxeGmoTWxZtLBL5AlGZF12KkVizFhFcbFwD0Gc0r3L&classification=print"
                        "&page=" 7 "&pageSize=" 100));5

(def test-data {:Items [{:Images "PS3_95.736_Fnt_DD_JP09.jpg", :Provenance "Robert S. Shaull [date and mode of acquisition unknown]; Walters Art Museum, 1990, by bequest.", :CreditLine "Bequest of Robert S. Shaull, 1990", :ExhibIDs "", :Medium "color pigments and ink on mulberry paper", :Description nil, :Style "Osaka School", :Reign nil, :Dynasty nil, :OnView 0, :Creator "Sadanobu I (Japanese, 1809-1879)", :ResourceURL "http://art.thewalters.org/detail/115", :Period "late Edo", :ObjectName "color woodcuts", :DateEndYear 1862, :Culture nil, :ObjectNumber "95.736", :GeoIDs "1492832", :CollectionID 1, :Collection "Asian Art", :DateBeginYear 1838, :Dimensions nil, :Inscriptions "[Signature] Sadanobu", :Classification "Prints", :ObjectID 115, :Title "Green Grocer Hanbei Traveling in the Country", :DisplayLocation "Not on View", :Keywords "Kabuki; festival; travel; genre", :DateText "ca. 1850", :PublicAccessDate "2014-12-06T01:00:00.213", :SortNumber "    95   736                                            ", :PrimaryImage {:Tiny "http://static.thewalters.org/images/PS3_95.736_Fnt_DD_JP09.jpg?width=50", :Small "http://static.thewalters.org/images/PS3_95.736_Fnt_DD_JP09.jpg?width=100", :Medium "http://static.thewalters.org/images/PS3_95.736_Fnt_DD_JP09.jpg?width=150", :Large "http://static.thewalters.org/images/PS3_95.736_Fnt_DD_JP09.jpg?width=250", :Raw "http://static.thewalters.org/images/PS3_95.736_Fnt_DD_JP09.jpg"}} 
                        {:Images "PS3_95.823_Fnt_DD_JP09.jpg", :Provenance "Mr. and Mrs. C. Robert Snell, Jr., Maryland Line, Maryland [date and mode of acquisition unknown]; Walters Art Museum, 1993, by gift.", :CreditLine "Gift of Mr. and Mrs. C. Robert Snell, Jr., 1993", :ExhibIDs "", :Medium "pigments on mulberry paper", :Description "This print depicts Fuwa Kazuemon Taira no Shigetane, a retainer.", :Style "Utagawa School", :Reign nil, :Dynasty nil, :OnView 0, :Creator "Yoshitora (Japanese, active ca. 1850-1880)", :ResourceURL "http://art.thewalters.org/detail/130", :Period "late Edo", :ObjectName "color woodcuts", :DateEndYear 1864, :Culture nil, :ObjectNumber "95.823", :GeoIDs "1490436", :CollectionID 1, :Collection "Asian Art", :DateBeginYear 1864, :Dimensions nil, :Inscriptions "[Signature] Yoshitora ga", :Classification "Prints", :ObjectID 130, :Title "Chushin gishi meimei den", :DisplayLocation "Not on View", :Keywords "Chushingura; samurai; revenge", :DateText "1864", :PublicAccessDate "2014-12-06T01:00:00.213", :SortNumber "    95   823                                            ", :PrimaryImage {:Tiny "http://static.thewalters.org/images/PS3_95.823_Fnt_DD_JP09.jpg?width=50", :Small "http://static.thewalters.org/images/PS3_95.823_Fnt_DD_JP09.jpg?width=100", :Medium "http://static.thewalters.org/images/PS3_95.823_Fnt_DD_JP09.jpg?width=150", :Large "http://static.thewalters.org/images/PS3_95.823_Fnt_DD_JP09.jpg?width=250", :Raw "http://static.thewalters.org/images/PS3_95.823_Fnt_DD_JP09.jpg"}}], 
                :PageSize 2, :Page 1, :NextPage true, :PrevPage false, :ReturnStatus true, :ReturnCode 200, :ReturnMessage []})

(defn my-value-reader [key value]
  (if (= key :date)
    (java.sql.Date/valueOf value)
    value))

(defn import_data 
  []
  (println "importing data")
  (:Items (json/read (clojure.java.io/reader get_data_url)
                     :value-fn my-value-reader
                     :key-fn keyword)))