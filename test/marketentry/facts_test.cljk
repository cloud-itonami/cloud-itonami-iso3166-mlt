(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest mlt-has-spec-basis
  (let [sb (facts/spec-basis "MLT")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "MLT")))
    (is (some? (facts/reserved-market-spec-basis "MLT")))))

(deftest mlt-rep-spec-basis-is-honestly-absent
  (testing "unlike Cyprus's real, current Article 57(1) proviso entry, MLT's representative-extension provision was not independently read this iteration -- honestly nil"
    (is (nil? (facts/rep-spec-basis "MLT")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "MLT")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "MLT" all)))
    (is (not (facts/required-evidence-satisfied? "MLT" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["MLT" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest reserved-market-spec-basis-urgency-notice-criteria
  (testing "the flagship urgency-notice-period gate's own criteria are recorded, kept under the `reserved-market-*` field names for cross-sibling-family parity"
    (let [rm (facts/reserved-market-spec-basis "MLT")]
      (is (= 15 (get-in rm [:reserved-market-criteria :urgency-notice-floor-days])))
      (is (= #{:duly-substantiated-urgency :extreme-urgency}
             (get-in rm [:reserved-market-criteria :urgency-bases]))))))
