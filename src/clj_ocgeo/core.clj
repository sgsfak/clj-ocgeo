(ns clj-ocgeo.core
  (:require
    [clojure.data.json :as json]
    [clojure.string :as string]
    [clj-http.client :as client]))

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
   place name returns its coordinates and optional annotations.
   
   The query `q` should be a description of a place or an address, see
   the [detailed guide](https://github.com/OpenCageData/opencagedata-misc-docs/blob/master/query-formatting.md)
   on how to format the query.
   `api-key` is the API key given during the registration with the API. 
   A number of optional keyword arguments can also be supplied:

    * `:abbrv`: If true, attempt to abbreviate and shorten the formatted string we return.
    * `:add_request`: If true, the various request parameters are added to the response for ease of debugging.
    * `:bounds`: This value will restrict the possible results to a defined bounding box.
       Its value hould be specified as a vector with two coordinate points forming ther
       south-west and north-east corners of a bounding box. For example:
       `[-0.563160,51.280430,0.278970,51.683979]` (min lon, min lat, max lon, max lat).
    * `:countrycode`: A string that restricts results to the specified country/territory or countries.
       The country code is a two letter code as defined by the ISO 3166-1 Alpha 2 standard.
       E.g. `gb` for the United Kingdom, `fr` for France, `us` for United States.
    * `:language`: An IETF format language code (such as `es` for Spanish or `pt-BR` for Brazilian Portuguese),r
       or `native` in which case we will attempt to return the response in the local language(s).
    * `:limit`: The maximum number of results we should return. Default is 10. Maximum allowable value is 100.
    * `:min_confidence`: An integer from 1-10. Only results with at least this confidence will be returned.
    * `:no_annotations`: When true results will not contain annotations.
    * `:no_dedupe`: When true results will not be deduplicated.
    * `:no_record`: When true the query contents are not logged.
    * `:proximity`: Provides the geocoder with a hint to bias results in favour of those
       closer to the specified location.
       The value is a vector of two elements representing a point with latitude,
       longitude coordinates in decimal format. For example: `[51.952659, 7.632473]`
    * `:roadinfo`:  When true the behaviour of the geocoder is changed to
       attempt to match the nearest road (as opposed to address).
       If possible we also fill additional information in the roadinfo annotation."
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
   or addresses.
   
   See [[forward-request]] for the options supported"
  [latitude longitude api-key & opts]
  (apply forward-request (string/join "," [latitude, longitude]) api-key opts))


(defn response-ok?
  "Checks whether the request was successful i.e. the returned status is 200"
  [response]
  (= 200 (-> response :status :code)))


