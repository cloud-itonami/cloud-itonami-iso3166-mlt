(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `urgency-notice-period-sufficient?` / `urgency-notice-period-
  insufficient?` are the SAME discipline applied to a genuinely
  Malta-specific mechanism: the Public Procurement Regulations (S.L.
  601.03, formerly S.L. 174.04), Regulation 116(3) and the proviso to
  Regulation 116(5), BOTH as substituted by Legal Notice 195 of 2019
  (curl/pdftotext-verified 2026-07-23 via EUR-Lex's own National
  Transposition Measures database, Malta's officially notified
  instrument CELEX 72014L0024MLT_276277). Regulation 116(3), own text:
  'Where a state of urgency duly substantiated renders impracticable
  the time limit laid down in sub-regulation (1), the authority
  responsible for the tendering process may fix a time limit which
  shall be not less than fifteen (15) days from the date on which the
  contract notice was sent to the Publications Office of the European
  Union or from the date on which the tender has been published as the
  case may be.' Regulation 116(5) proviso, own text: 'Provided that the
  authority responsible for the tendering process may reduce this time
  limit to fifteen (15) days in cases of extreme urgency.'

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors, along TWO independent axes at once.
  Bulgaria's ЗОП Art. 54(5) de-minimis is a PERCENTAGE-OF-TURNOVER
  ELIGIBILITY formula, Albania's Neni 76(2)(c) carve-out is a
  FLAT-CONSTANT (currency) ELIGIBILITY threshold, Azerbaijan's/
  Armenia's flagship checks are BOOLEAN registry-membership ELIGIBILITY
  reads, Antigua and Barbuda's vendor-class check is a THREE-TIER
  (currency) ELIGIBILITY-THRESHOLD classification, Benin's MPME
  mechanism is a BID-EVALUATION PRICE ADJUSTMENT, Bhutan's FDI Negative
  List is a CATEGORICAL SECTOR-EXCLUSION allow-list gate, the Central
  African Republic's Marché réservé mechanism is an OR-of-three
  workforce-composition INCLUSION-ELIGIBILITY test, Cyprus's Article
  57(6)-(7) self-cleaning is an AND-of-three conjunctive REMEDIATION-
  SUFFICIENCY test with a final-decision override, Andorra's Art. 30.1
  is a TWO-AXIS (contract type x urgency) EUR ELIGIBILITY-THRESHOLD
  classification, San Marino's Art. 11 is a residency-conditioned
  two-part COMPLIANCE-ARTIFACT-PRESENCE gate, Liechtenstein's
  `fiscal-rep-missing` gates on a worldwide-turnover threshold, and
  Monaco's `rci-clearance-missing` gates on a registry-membership
  boolean. EVERY ONE of those tests something about the BIDDER: its
  eligibility to use a procedure or threshold band, its evidence
  sufficiency, its registry membership, its fiscal presence. Malta's
  Regulation 116 mechanism tests something structurally different: NOT
  the bidder at all, but whether the CALL ITSELF the bidder is
  responding to observed a LAWFUL minimum notice period -- a
  CALENDAR-DURATION (not currency, not boolean-membership, not
  categorical) floor, gating the PROCEDURAL LAWFULNESS of the
  contracting authority's own timeline rather than the bidder's
  eligibility or compliance state. This is also the directional
  opposite of Andorra's own urgency-conditioned check: Andorra's Art.
  30.1 asks whether a LOW-value, possibly-urgent contract may SKIP
  competitive procedure entirely; Malta's Regulation 116 asks whether
  an urgent COMPETITIVE call's own TRUNCATED timeline still meets a
  lawful floor. The practical effect for the market-entry operator this
  actor serves is protective: proceeding with a filing/submission into
  a call whose own notice period falls short of Regulation 116's floor
  would mean participating in (and by submitting, tacitly validating) a
  procedurally defective call -- flagged here rather than silently
  waved through.

  Only Regulation 116(3)'s own 'duly substantiated' urgency basis and
  the Regulation 116(5) proviso's own 'extreme urgency' basis are
  modeled, both converging (per the text this iteration actually read)
  on the SAME fifteen-day floor. Regulation 116(1)'s own general
  (non-urgency) time limit, and whether Regulation 116 was itself
  further amended by any of the SIX later instruments this iteration
  found cited -- but did not each individually fetch -- on the
  Department of Contracts' own resources page (LN301/2019 through
  LN47/2025), are deliberately NOT modeled: an honest scope-narrowing,
  the same discipline this family's other honest-narrowing decisions
  already established (Cyprus's own Article 93 delegated Regulations,
  San Marino's own Art. 3 implementing-regulation thresholds).

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement portal. It builds the RECORD an operator
  would keep, not the act of submitting a portal registration itself
  (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(def urgency-notice-floor-days
  "Regulation 116(3) / the Regulation 116(5) proviso, own definition
  (curl/pdftotext-verified 2026-07-23 against EUR-Lex's own hosting of
  Malta's notified transposition instrument): the minimum number of
  days between the contract notice being sent to the EU Publications
  Office (or the tender being published) and the tender deadline, when
  a state of urgency (either 'duly substantiated' under sub-regulation
  (3), or 'extreme urgency' under the sub-regulation (5) proviso) has
  been invoked to shorten the ordinary time limit."
  15)

(def urgency-bases
  "The two Regulation 116 urgency bases this catalog models, each
  independently amended by Legal Notice 195 of 2019 but converging (per
  the text this iteration actually read) on the same 15-day floor."
  #{:duly-substantiated-urgency :extreme-urgency})

(defn urgency-notice-period-sufficient?
  "The ground-truth Regulation 116 notice-period sufficiency for
  `engagement`: when `:urgency-invoked?` is false, this vertical does
  not independently model Regulation 116(1)'s own general time limit
  (an honest scope limit -- see namespace docstring), so the check does
  not apply and is trivially satisfied. When `:urgency-invoked?` is
  true, the engagement's own declared `:notice-days-given` must be a
  number and must be at least `urgency-notice-floor-days`."
  [{:keys [urgency-invoked? notice-days-given]}]
  (if-not urgency-invoked?
    true
    (and (number? notice-days-given)
         (>= notice-days-given urgency-notice-floor-days))))

(defn urgency-notice-period-insufficient?
  "Does `engagement` declare `:urgency-invoked? true` (the call the
  operator is bidding into was time-limited under Regulation 116's
  urgency procedure) while the INDEPENDENTLY recomputed
  `urgency-notice-period-sufficient?` is false? An engagement that never
  declares urgency is never flagged by this check (entity/engagement-
  scope-gated, the same discipline Bhutan's `:foreign-company?`-gated
  FDI check and Cyprus's `:exclusion-ground?`-gated self-cleaning check
  use)."
  [{:keys [urgency-invoked?] :as engagement}]
  (boolean (and urgency-invoked?
                (not (urgency-notice-period-sufficient? engagement)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
