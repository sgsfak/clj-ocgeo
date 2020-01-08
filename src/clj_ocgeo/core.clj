(ns clj-ocgeo.core
  (:require [clojure.data.json :as json])
  (:require [clojure.string :as string])
  (:require [clj-http.lite.client :as client]))

(defn- build-qs [options]
  (let [truthy? #(and % (not= 0 %) (not (empty? %)))
        bool-to-int #(if (truthy? %) 1 0)
        vec-to-str #(string/join "," %)
        when-update (fn [opts kw f] (if (truthy? (kw opts))
                                      (update opts kw f)
                                      (dissoc opts kw)))]
    (-> options
        (when-update :abbrev bool-to-int)
        (when-update :no_annotations bool-to-int)
        (when-update :no_dedupe bool-to-int)
        (when-update :no_record bool-to-int)
        (when-update :roadinfo bool-to-int)
        (when-update :add_request bool-to-int)
        (when-update :bounds vec-to-str)
        (when-update :proximity vec-to-str))))

(defn forward-request
  "Makes forward geocoding request which given an address or
   place name returns its coordinates and optional annotations."
  [query api-key & {:keys [abbrev bounds countrycode
                           language limit min_confidence
                           no_annotations no_dedupe
                           no_record proximity roadinfo
                           add_request]
                    :as options}]
  (let [qs (-> options build-qs (merge {"q" query "key" api-key}))
        response (client/get "https://api.opencagedata.com/geocode/v1/json"
                             {:accept :json
                              :save-request? true
                              :throw-exceptions false
                              :query-params qs})
        url (-> response :request :http-url)]
    (-> response :body (json/read-str :key-fn keyword) (assoc :URL url))))


(defn reverse-request
  "Make a reverse request, i.e. given a latitude and a longitude
   coordinates it returns a list of human understandable place names
   or addresses"
  [latitude longitude api-key & opts]
  (apply forward-request (string/join "," [latitude, longitude]) api-key opts))


(defn response-ok?
  "Checks whether the request was successful i.e. the returned status is 200"
  [response]
  (= 200 (-> response :status :code)))


