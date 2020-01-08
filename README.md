# clj-ocgeo

A Clojure library for accessing the [OpenCage Geocoding API](https://opencagedata.com/)

[![Clojars Project](https://img.shields.io/clojars/v/clj-ocgeo.svg)](https://clojars.org/clj-ocgeo)

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



