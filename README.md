# clj-ocgeo

A Clojure library for accessing the [OpenCage Geocoding API](https://opencagedata.com/)

[![Clojars Project](https://img.shields.io/clojars/v/clj-ocgeo.svg)](https://clojars.org/clj-ocgeo) [![cljdoc badge](https://cljdoc.org/badge/clj-ocgeo/clj-ocgeo)](https://cljdoc.org/badge/clj-ocgeo/clj-ocgeo/CURRENT)


## Getting started

Simply add `clj-ocgeo` as a dependency to your lein project:

```clojure
[clj-ocgeo "0.2.0"]
```

Then execute

```
lein deps
```

And import the library into your namespace

```clojure
(:use clj-ocgeo.core)
```

Or  in the "repl":

```clojure
(require '[clj-ocgeo.core :refer :all])
```

## Usage

The client should have an API key by [registering](https://opencagedata.com/users/sign_up) with the OpenCage Geocoder. It's also always good to check the [best practices for using the OpenCage API](https://opencagedata.com/api#bestpractices), and in particular [how to format forward geocoding queries](https://github.com/OpenCageData/opencagedata-roadmap/blob/master/query-formatting.md).

To make a "forward" request to get the coordinates of a given place or address, the following function call can be used:

```clojure
(forward-request "Friedrich-Ebert-Straße 7, 48153 Münster, Germany" "6d0e711d72d74daeb2b0bfd2a5cdfdba")
```

Or a "reverse" request (i.e. given the latitude and longitude coordinates, it returns a list of human understandable place names or addresses):

```clojure
(forward-request 51.9526599 7.632473 "6d0e711d72d74daeb2b0bfd2a5cdfdba")
```

Both functions return a [Clojure Map](https://clojure.org/guides/learn/hashed_colls#_maps) with keyword keys so for example to get the status code of the `response` you can do:

```clojure
(:status response)
;; Returns:
;; {:code 200, :message "OK"}
```

(For this specific example, you can also call the `response-ok?` helper function  that returns `true` when the status code is 200)

A number of keyword arguments corresponding to the [optional parameters](https://opencagedata.com/api#forward-opt) of the OpenCage Geocoding API can be also supplied to both "request" functions, e.g.

```clojure
(forward-request 51.9526599 7.632473 "6d0e711d72d74daeb2b0bfd2a5cdfdba"
		:countrycode "de" :no_annotations true :min_confidence 5)
```

Another example, showing the first result of a request is shown below:

```clojure
(when-let [r (-> (forward-request "Friedrich-Ebert-Straße 7" "6d0e711d72d74daeb2b0bfd2a5cdfdba")
                 :results
                 first)]
  (pprint r))

;; {:annotations
;;  {:timezone
;;   {:name "Europe/Berlin",
;;    :now_in_dst 0,
;;    :offset_sec 3600,
;;    :offset_string "+0100",
;;    :short_name "CET"},
;;   :roadinfo
;;   {:drive_on "right",
;;    :road "Friedrich-Ebert-Straße",
;;    :speed_in "km/h"},
;;   :geohash "u1jrt9ty1t8rg3r5wttm",
;;   :what3words {:words "episodes.mass.below"},
;;   :flag "🇩🇪",
;;   :OSM
;;   {:edit_url
;;    "https://www.openstreetmap.org/edit?way=125838041#map=17/51.95266/7.63247",
;;    :note_url
;;    "https://www.openstreetmap.org/note/new#map=17/51.95266/7.63247&layers=N",
;;    :url
;;    "https://www.openstreetmap.org/?mlat=51.95266&mlon=7.63247#map=17/51.95266/7.63247"},
;;   :currency
;;   {:symbol "€",
;;    :name "Euro",
;;    :thousands_separator ".",
;;    :iso_numeric "978",
;;    :subunit_to_unit 100,
;;    :iso_code "EUR",
;;    :decimal_mark ",",
;;    :alternate_symbols [],
;;    :symbol_first 1,
;;    :smallest_denomination 1,
;;    :subunit "Cent",
;;    :html_entity "&#x20AC;"},
;;   :MGRS "32UMC0602156656",
;;   :Mercator {:x 849643.007, :y 6757899.137},
;;   :qibla 128.55,
;;   :sun
;;   {:rise
;;    {:apparent 1578468960,
;;     :astronomical 1578461580,
;;     :civil 1578466560,
;;     :nautical 1578463980},
;;    :set
;;    {:apparent 1578497760,
;;     :astronomical 1578505140,
;;     :civil 1578500160,
;;     :nautical 1578502740}},
;;   :DMS {:lat "51° 57' 9.57564'' N", :lng "7° 37' 56.90280'' E"},
;;   :callingcode 49,
;;   :Maidenhead "JO31tw58vp",
;;   :UN_M49
;;   {:regions
;;    {:DE "276", :EUROPE "150", :WESTERN_EUROPE "155", :WORLD "001"},
;;    :statistical_groupings ["MEDC"]}},
;;  :bounds
;;  {:northeast {:lat 51.9528202, :lng 7.6325938},
;;   :southwest {:lat 51.9525445, :lng 7.6323594}},
;;  :components
;;  {:city_district "Münster-Mitte",
;;   :suburb "Innenstadtring",
;;   :continent "Europe",
;;   :political_union "European Union",
;;   :neighbourhood "Josef",
;;   :house_number "7",
;;   :city "Münster",
;;   :_type "building",
;;   :county "Münster",
;;   :postcode "48153",
;;   :state "North Rhine-Westphalia",
;;   :ISO_3166-1_alpha-3 "DEU",
;;   :country_code "de",
;;   :state_district "Regierungsbezirk Münster",
;;   :ISO_3166-1_alpha-2 "DE",
;;   :country "Germany",
;;   :road "Friedrich-Ebert-Straße"},
;;  :confidence 10,
;;  :formatted "Friedrich-Ebert-Straße 7, 48153 Münster, Germany",
;;  :geometry {:lat 51.9526599, :lng 7.632473}}
nil
```

