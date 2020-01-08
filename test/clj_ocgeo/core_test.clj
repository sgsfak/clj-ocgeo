(ns clj-ocgeo.core-test
  (:require [clojure.test :refer :all]
            [clj-ocgeo.core :refer :all]))

(def good-test-key "6d0e711d72d74daeb2b0bfd2a5cdfdba")

(deftest authentication-test
  (testing "the API key is incorrect"
    (let [resp (forward-request "New York" "NON-EXISTENT-API-KEY")
          status (-> resp :status :code)]
      (is (= status 401)))))


(deftest quota-test
  (testing "the user is out of quota"
    (let [resp (forward-request "New York" "4372eff77b8343cebfc843eb4da4ddc4")
          status (-> resp :status :code)]
      (is (= status 402)))))

(deftest forbidden-test
  (testing "API key blocked"
    (let [resp (forward-request "New York" "2e10e5e828262eb243ec0b54681d699a")
          status (-> resp :status :code)]
      (is (= status 403)))))


(deftest rate-limited-test
  (testing "Rate limit exceeded"
    (let [resp (forward-request "New York" "d6d0f0065f4348a4bdfe4587ba02714b")
          status (-> resp :status :code)]
      (is (= status 429)))))

(deftest returned-data-test
  (testing "Successful request"
    (let [resp (reverse-request 51.9526599 7.632473 good-test-key)
          status (-> resp :status :code)]
      (is (= status 200))
      (is (= (-> resp :results first :annotations :what3words :words)
             "episodes.mass.below")))))

