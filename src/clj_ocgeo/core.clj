(ns clj-ocgeo.core
  (:require [clojure.data.json :as json])
  (:require [clj-http.lite.client :as client]))

(defn- when-update [x pred f & args]
  (if (pred x)
    (apply f x args)
    x))

(defn- build-qs [options]
  (-> options 
      (when-update #(true? (:abbrev %)) #(assoc % :abbrev 1))
      (when-update #(true? (:no_annotations %)) #(assoc % :no_annotations 1))
      (when-update #(true? (:no_dedupe %)) #(assoc % :no_dedupe 1))
      (when-update #(true? (:no_record %)) #(assoc % :no_record 1))
      (when-update #(true? (:roadinfo %)) #(assoc % :roadinfo 1))
      (when-update #(not (empty? (:bounds %))) 
                   #(assoc % :bounds (clojure.string/join "," (:bounds %))))
      (when-update #(not (empty? (:proximity %)))
                   #(assoc % :proximity (clojure.string/join "," (:proximity %))))))

(defn make-request [query api-key & {:keys [abbrev bounds countrycode 
                                            language limit min_confidence
                                            no_annotations no_dedupe
                                            no_record proximity roadinfo]
                                     :or {abbrev false ;bounds countrycode 
                                          bounds []
                                          limit 10
                                          no_annotations false}
                                     :as options}]
  (let [qs (-> options build-qs (merge {"q" query "key" api-key}))
        response (client/get "https://api.opencagedata.com/geocode/v1/json"
                             {:accept :json
                              :throw-exceptions false
                              :query-params qs})]
    (-> response :body (json/read-str :key-fn keyword))))

(defn response-ok? [response]
  (= 200 (-> response :status :code)))

