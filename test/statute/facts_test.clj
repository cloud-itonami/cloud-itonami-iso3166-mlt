(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest mlt-has-spec-basis
  (let [sb (facts/spec-basis "MLT")]
    (is (= 5 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["MLT" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["mlt.companies-act-cap386"]
         (mapv :statute/id (facts/by-topic "MLT" :corporate-governance))))
  (is (= ["mlt.employment-industrial-relations-act-cap452"]
         (mapv :statute/id (facts/by-topic "MLT" :labor))))
  (is (= ["mlt.income-tax-act-cap123"]
         (mapv :statute/id (facts/by-topic "MLT" :tax))))
  (is (= #{"mlt.malta-enterprise-act-cap463" "mlt.business-promotion-act-cap325"}
         (set (mapv :statute/id (facts/by-topic "MLT" :foreign-investment)))))
  (is (empty? (facts/by-topic "MLT" :data-protection))
      "a Malta-specific data-protection implementing act was not independently verified this iteration -- honestly absent, see namespace docstring")
  (is (empty? (facts/by-topic "ATL" :labor))))
