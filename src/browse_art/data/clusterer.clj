(ns browse-art.data.clusterer
  (:use [clj-ml.data]
        [clj-ml.clusterers]))

(def ds (make-dataset "name" [:length :width {:kind [:good :bad]}] [ [12 34 :good] [24 53 :bad] ]))
(dataset-remove-attribute-at ds 1)
(def kmeans (make-clusterer :k-means {:number-clusters 3}))

(clusterer-build kmeans ds)

;(.value (second (clusterer-cluster kmeans ds)) 2)