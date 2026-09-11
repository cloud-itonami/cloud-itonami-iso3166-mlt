(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "MLT" 0)
        s (registry/register-submit "eng-1" "MLT" 0)]
    (is (= "MLT-DFT-000000" (get d "draft_number")))
    (is (= "MLT-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "MLT" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest urgency-notice-period-sufficient-not-gated-when-urgency-not-invoked
  (testing "Regulation 116(1)'s own general (non-urgency) time limit is out of scope -- the check is trivially satisfied when urgency was never invoked, even with no declared notice period at all"
    (is (true? (registry/urgency-notice-period-sufficient?
                {:urgency-invoked? false :notice-days-given nil})))))

(deftest urgency-notice-period-sufficient-requires-the-15-day-floor
  (testing "Regulation 116(3) / the Regulation 116(5) proviso's own fifteen-day floor"
    (is (true? (registry/urgency-notice-period-sufficient?
                {:urgency-invoked? true :notice-days-given 15})))
    (is (true? (registry/urgency-notice-period-sufficient?
                {:urgency-invoked? true :notice-days-given 20})))
    (is (false? (registry/urgency-notice-period-sufficient?
                 {:urgency-invoked? true :notice-days-given 14})))
    (is (false? (registry/urgency-notice-period-sufficient?
                 {:urgency-invoked? true :notice-days-given nil}))
        "a missing declared notice period, once urgency IS invoked, cannot be assumed sufficient")))

(deftest urgency-notice-period-insufficient-is-entity-scope-gated
  (testing "an engagement that never declares urgency is never flagged by this check, even with an absurdly short (or absent) notice period"
    (is (false? (registry/urgency-notice-period-insufficient?
                 {:urgency-invoked? false :notice-days-given 1}))))
  (testing "an urgency-invoked engagement whose declared notice period falls short of the 15-day floor -> flagged"
    (is (true? (registry/urgency-notice-period-insufficient?
                {:urgency-invoked? true :notice-days-given 10}))))
  (testing "an urgency-invoked engagement whose declared notice period meets the floor -> not flagged"
    (is (false? (registry/urgency-notice-period-insufficient?
                 {:urgency-invoked? true :notice-days-given 15})))))
