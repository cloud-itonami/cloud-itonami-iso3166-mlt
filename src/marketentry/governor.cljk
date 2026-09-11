(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Malta procurement law, whether a claimed engagement fee
  actually equals base + months x rate, whether a call the engagement
  is bidding into actually observed Regulation 116's own minimum
  tender-notice-period floor when urgency was invoked, whether a
  Commissioner for Revenue tax record has been verified for a filing
  that requires it, or when a draft stops being a draft and becomes a
  real-world ePPS (Electronic Public Procurement System) submission, so
  this MUST be a separate system able to *reject* a proposal and fall
  back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Urgency notice insufficient -- for `:filing/submit`, when the
                                       engagement declares
                                       `:urgency-invoked? true` (i.e.
                                       the call it is bidding into was
                                       time-limited under Regulation
                                       116's urgency procedure),
                                       INDEPENDENTLY recompute whether
                                       the engagement's own declared
                                       `:notice-days-given` actually
                                       satisfies the Public Procurement
                                       Regulations' own Regulation
                                       116(3) / Regulation 116(5)
                                       proviso fifteen-day floor, and
                                       HARD-hold if not. FLAGSHIP
                                       genuinely new check for the
                                       iso3166 family (grep-verified
                                       absent as a governor check
                                       function name fleet-wide at
                                       build time) -- a CALENDAR-
                                       DURATION floor gating the
                                       PROCEDURAL LAWFULNESS of the
                                       call's own timeline, not the
                                       bidder's eligibility or
                                       compliance state -- a check
                                       SHAPE and a check SUBJECT
                                       genuinely different from every
                                       prior sibling's (turnover
                                       formula / flat currency
                                       threshold / boolean registry
                                       membership / 3-tier currency
                                       threshold / bid-evaluation price
                                       adjustment / categorical
                                       sector-exclusion allow-list /
                                       OR-of-three workforce-composition
                                       inclusion test / AND-of-three
                                       remediation-sufficiency test /
                                       two-axis currency-threshold
                                       classification / residency-
                                       conditioned artifact-presence
                                       gate / fiscal-rep-missing
                                       turnover threshold) -- the first
                                       in this family to test whether
                                       the CALL ITSELF (not the bidder)
                                       met a procedural-lawfulness
                                       floor.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. Tax record unverified        -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-tax-record? true`,
                                       INDEPENDENTLY check
                                       `:tax-record-verified?`.
                                       CONDITIONAL on the engagement's
                                       own ground truth. Grounded in the
                                       Commissioner for Revenue / Income
                                       Tax Act, Cap. 123 (see
                                       `marketentry.facts`).
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(MBR登録/税務記録/ePPS登録/緊急通知期間確認/代理人確認等)が充足していない状態での提案"}]))))

(defn- urgency-notice-insufficient-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the call the
  engagement is bidding into observed the Public Procurement
  Regulations' own Regulation 116(3) / Regulation 116(5) proviso
  fifteen-day minimum notice period -- the flagship check this vertical
  adds. HARD-hold when the engagement declares `:urgency-invoked? true`
  but is not independently sufficient."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/urgency-notice-period-insufficient? e)
        [{:rule :urgency-notice-insufficient
          :detail (str subject " は緊急手続(Regulation 116)を宣言した公告への入札を"
                      "提出しようとしているが、独立再計算による通知期間(:notice-days-given)"
                      "が Regulation 116(3)/(5)但書の定める最低15日の閾値を満たさない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- tax-record-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-tax-record? true`, INDEPENDENTLY check
  `:tax-record-verified?` -- CONDITIONAL on the engagement's own ground
  truth. Grounded in the Commissioner for Revenue / Income Tax Act,
  Cap. 123."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-tax-record? e))
                 (not (true? (:tax-record-verified? e))))
        [{:rule :tax-record-unverified
          :detail (str subject " はCommissioner for Revenue税務記録の確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (urgency-notice-insufficient-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (tax-record-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
