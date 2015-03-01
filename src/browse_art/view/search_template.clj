(ns browse-art.view.search-template
  (:use [hiccup.page :only (html5 include-css include-js)]))

(defn layout 
  [query objects]
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
     [:section {:class "gallery" }
      (map
        (fn
          [[id image]]
			      [:a {:href (str "/object/" id) } 
			       [:img {:src (str "http://static.thewalters.org/images/" image "?width=100")}]])
	      objects)]
     [:aside {:class "gallery" }
      [:div
       [:form {:id "searchform" :method "GET" :action "/search"}
        [:input {:type "text" :placeholder "Search" :class "searchinput" :name "query" :value query
                 :size "21" :maxlength "120"}]]]
      [:div 
       [:p "Type keywords to search through various types of art works."]
       [:p "For example: flower home."]
       [:span {:class "label back"} 
        [:a {:href "/search"} "Reset search"]]]]
     [:footer 
      [:center "Copyright &#169; 2015 Jelena Đorđević " 
       [:a {:href "mailto:jelena927@gmail.com"} "jelena927@gmail.com"]]]
     ]))