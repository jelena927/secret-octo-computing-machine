BrowseArt
===============
# 1. About the project
The idea of this project is to create an application for advanced searching and ranking art works using key words that describe an artwork. 
Information about art works are obtained from [The Walters Art Museum public API](http://api.thewalters.org/). 

- Data first go to indexer which stores information about the object and extract words useful for search. Words are ranked by their importance for particular object. Here is used algorithm from the [Programming Collective Intelligence](http://it-ebooks.info/book/330/) book, section 4.
- After indexing data goes to clustering, where similar objects are grouped by key words. Groups are created using K-Means Clustering method. For this is used [clj-ml](https://github.com/joshuaeckroth/clj-ml), a machine learning library for Clojure built on top of Weka framework.

# 2. Technical realisation
Application is written in [Clojure](http://clojure.org/) programming language. Database is [SQLite](https://sqlite.org/) Libraries used are [Clojure Soup](https://github.com/mfornos/clojure-soup), [clojure.java.jdbc](https://github.com/clojure/java.jdbc), [data.json](https://github.com/clojure/data.json), [Compojure](https://github.com/weavejester/compojure), [Hiccup](https://github.com/weavejester/hiccup), [Ring](https://github.com/ring-clojure/ring/), [clj-ml](https://github.com/joshuaeckroth/clj-ml), [clojure.jdbc-c3p0](https://github.com/niwibe/clojure.jdbc-c3p0).

# 3. Running the application
To run the application install [Leiningen](http://leiningen.org/) version 2.4.2, then go to root folder of application and run from the command line:
`lein run`
When server is started type http://localhost:8080/ in your browser address bar.

# 4. Acknowledgements
This application has been developed as a part of the project assignment for the subjects [Tools and Methods of Software Engineering](http://ai.fon.bg.ac.rs/alati-i-metode-softverskog-inzenjerstva), [Intelligent Information Systems](http://ai.fon.bg.ac.rs/inteligentni-informacioni-sistemi) and [Applied Artificial Intelligence](http://ai.fon.bg.ac.rs/primene-vestacke-inteligencije) at the Faculty of Organization Sciences, University of Belgrade, Serbia.

# 5. Licence
This software is licensed under the [MIT License](http://opensource.org/licenses/MIT).

The MIT License (MIT)

Copyright (c) 2015 Jelena Djordjevic - jelena927@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
