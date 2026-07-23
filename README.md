# cloud-itonami-iso3166-mlt

Open ISO 3166 Blueprint for **MLT**: Republic of Malta.

Independent public-sector market-entry & procurement-compliance service
for an already-incorporated operator entering public contracts in
Malta.

- Department of Contracts (Dipartiment tal-Kuntratti), within the
  portfolio of the Ministry for Finance (MFIN) -- Competent Authority
  for Public Procurement, Public Procurement Regulations (S.L. 601.03);
  ePPS (Electronic Public Procurement System) e-tendering portal
- Malta Business Registry (MBR, established under S.L. 595.27, led by
  the Registrar of Companies) incorporation record, Companies Act
  (Cap. 386); Commissioner for Revenue tax record, Income Tax Act
  (Cap. 123)
- Public Procurement Regulations, Regulation 116(3) / Regulation
  116(5) proviso minimum-tender-notice-period gate (Legal Notice 195 of
  2019)

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as the other `cloud-itonami-iso3166-*` siblings:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Department
  of Contracts (Dipartiment tal-Kuntratti, within the Ministry for
  Finance's portfolio), the Public Procurement Regulations (S.L.
  601.03, originally Legal Notice 352 of 2016, most recently amended by
  Legal Notice 47 of 2025), the ePPS e-tendering portal
  (Electronic Public Procurement System), the Malta Business Registry
  (MBR, S.L. 595.27, Registrar of Companies, BAROS portal) and the
  Commissioner for Revenue / Income Tax Act (Cap. 123). `governor.cljc`'s
  flagship check independently recomputes whether a call the engagement
  is bidding into, when it declares `:urgency-invoked? true`, observed
  the Public Procurement Regulations' own Regulation 116(3) / Regulation
  116(5) proviso fifteen-day minimum tender-notice-period floor (as
  substituted by Legal Notice 195 of 2019) -- a CALENDAR-DURATION
  procedural-lawfulness check on the CALL itself, not a bidder
  eligibility gate, a check shape genuinely different from every other
  iso3166 sibling's (see the namespace docstrings for the full research
  trail, including working assumptions that did NOT survive
  verification and gaps this iteration honestly could not close).
- `src/statute/facts.cljc` -- general-law catalog: the Companies Act
  (Cap. 386), the Employment and Industrial Relations Act (Cap. 452,
  administered by Jobsplus), the Malta Enterprise Act (Cap. 463) and
  Business Promotion Act (Cap. 325 + S.L. 325.6), and the Income Tax
  Act (Cap. 123).

Every citation traces to a source this iteration actually fetched and
read this session (2026-07-23): `contracts.gov.mt`'s own English pages
(via the Internet Archive Wayback Machine -- the live site returns a
Cloudflare bot-detection challenge, which this iteration did NOT
attempt to bypass), `mbr.mt` and `jobsplus.gov.mt` and
`maltaenterprise.com` (all live, direct HTTP 200 fetches), EUR-Lex's
own National Transposition Measures database (Malta's officially
notified transposition instrument for Directive 2014/24/EU, downloaded
as a real PDF and read via `pdftotext`), and `legislation.mt`'s own
structured (JSON-LD) metadata (title / in-force status / dates only --
`legislation.mt` itself is a JavaScript single-page application that
this iteration confirmed does NOT serve substantive article text to a
plain HTTP fetch or to WebFetch, a distinct and honestly disclosed
technical limitation, NOT a bot-detection block). This iteration also
recorded which working assumptions did NOT survive verification (e.g.
'MFSA' as the companies registrar -- the real registrar is the
standalone Malta Business Registry, MBR) -- see `marketentry.facts` and
`statute.facts`'s docstrings for the full citation trail and every
honestly disclosed gap.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Malta:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
