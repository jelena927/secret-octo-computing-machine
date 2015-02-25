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
			      [:a {:href (str "/" id) } 
			       [:img {:src (str "http://static.thewalters.org/images/" image "?width=100")}]])
	      objects)]
     [:aside
      [:div
       [:form {:id "searchform" :method "GET" :action "/search"}
        [:input {:type "text" :placeholder "Search" :class "searchinput" :name "query" :value query
                 :size "21" :maxlength "120"}]]]
      [:div "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
			tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
			quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
			consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
			cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
			proident, sunt in culpa qui officia deserunt mollit anim id est laborum."]]
     [:footer 
      [:center "Copyright &#169; 2015 jelena927@gmail.com"]]
     ]))