(ns culture.facts
  "Country-level regional-culture catalog for Malta (MLT) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"MLT"
   [{:culture/id "mlt.dish.pastizz"
     :culture/name "Pastizzi"
     :culture/name-local "Pastizz"
     :culture/country "MLT"
     :culture/kind :dish
     :culture/summary "Traditional savoury pastry from Malta, diamond- or round-shaped, filled with either ricotta or curried peas and baked at small family pastizzerias."
     :culture/url "https://en.wikipedia.org/wiki/Pastizz"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mlt.dish.stuffat-tal-fenek"
     :culture/name "Rabbit stew"
     :culture/name-local "Stuffat tal-Fenek"
     :culture/country "MLT"
     :culture/kind :dish
     :culture/summary "Malta's official national dish: rabbit slow-cooked with wine, tomatoes, garlic and seasonings, in Maltese cuisine."
     :culture/url "https://en.wikipedia.org/wiki/Stuffat_tal-Fenek"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mlt.dish.ftira"
     :culture/name "Ftira"
     :culture/country "MLT"
     :culture/kind :dish
     :culture/summary "Ring-shaped, leavened Maltese bread usually eaten with fillings such as sardines, tuna, potato, tomato, onion, capers and olives; added to UNESCO's Intangible Cultural Heritage List in 2020."
     :culture/url "https://en.wikipedia.org/wiki/Ftira"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mlt.beverage.kinnie"
     :culture/name "Kinnie"
     :culture/country "MLT"
     :culture/kind :beverage
     :culture/summary "Maltese bittersweet carbonated soft drink brewed from bitter oranges and extracts of wormwood, first introduced in 1952 and regarded as Malta's iconic non-alcoholic drink."
     :culture/url "https://en.wikipedia.org/wiki/Kinnie"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mlt.product.gbejna"
     :culture/name "Ġbejna"
     :culture/country "MLT"
     :culture/kind :product
     :culture/summary "Small round cheese made in Malta and Gozo from sheep milk, salt and rennet, a traditional Maltese food product."
     :culture/url "https://en.wikipedia.org/wiki/%C4%A0bejna"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mlt.craft.maltese-lace"
     :culture/name "Maltese lace"
     :culture/name-local "Bizzilla"
     :culture/country "MLT"
     :culture/kind :craft
     :culture/summary "Style of bobbin lace made in Malta that developed from Genoese needle lace patterns in the mid-1800s and became particularly popular on the island of Gozo."
     :culture/url "https://en.wikipedia.org/wiki/Maltese_lace"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mlt.festival.carnival"
     :culture/name "Maltese Carnival"
     :culture/country "MLT"
     :culture/kind :festival
     :culture/summary "Festival held during the week leading up to Ash Wednesday in Malta, with masked balls, fancy dress and float parades, celebrated since at least the mid-15th century."
     :culture/url "https://en.wikipedia.org/wiki/Carnival_in_Malta"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mlt.heritage.ggantija"
     :culture/name "Ġgantija"
     :culture/country "MLT"
     :culture/kind :heritage
     :culture/summary "Neolithic megalithic temple complex on the island of Gozo, built around 3600 BC, part of the \"Megalithic Temples of Malta\" UNESCO World Heritage Site inscribed in 1980."
     :culture/url "https://en.wikipedia.org/wiki/%C4%A0gantija"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-mlt culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "MLT"))
                 " MLT entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
