(ns browse-art.view.object-template
  (:use [hiccup.page :only (html5 include-css include-js)]))

(defn layout 
  [obj recommendation]
  (html5 
    [:head
     [:meta {:charset "utf=8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
     [:meta {:name "template" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
     [:title "Browse Art"]
     (include-css "/css/style.css")
     (include-js "/js/script.js")]
    [:body
     [:header 
      [:center
       [:h1 "Browse Art"]]]
     [:section {:class "details" }
      [:object {:data (str "http://static.thewalters.org/images/" (:image obj) "?width=600")}]
      [:div {:class "recommendation"}
       [:p "See also:"]
	       (map
	        (fn
	          [r]
				      [:a {:href (str "/object/" (:object_id r)) } 
				       [:img {:src (str "http://static.thewalters.org/images/" (:image r) "?height=100")}]])
		      recommendation)]]
     [:aside {:class "details" }
      [:div
       [:span {:class "label"} "Title:"] [:span (:title obj)][:br]
       [:span {:class "label"} "Creator:"] [:span (:creator obj)][:br]
       [:span {:class "label"} "Style:"] [:span (:style obj)][:br]
       [:span {:class "label"} "Collection:"] [:span (:collection obj)][:br]
       [:span {:class "label"} "Period:"] [:span (:period obj)][:br]
       [:span {:class "label"} "Description:"] [:span (:description obj)][:br]
       [:span {:class "label"} "Classification:"] [:span (:classification obj)][:br]
       [:span {:class "label"} "Medium:"] [:span (:medium obj)][:br]
       [:span {:class "label"} "TAGS:"] [:span (:keywords obj)][:br]
       [:span {:class "label back"} 
        [:a {:href "/search"} "Go Back To Search"]]]]
     [:footer 
      [:center "Copyright &#169; 2015 Jelena Đorđević " 
       [:a {:href "mailto:jelena927@gmail.com"} "jelena927@gmail.com"]]]
     ]))