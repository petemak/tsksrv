(ns tsksrv.datomic
  (:require [datomic.api :as d]))

;; -----------------------------------------------
;; Schema describes two entities
;; 1. car with four attributes make, model, year and 
;; 2. customer/owner
(def schema [{:db/ident :car/make
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "Brand of car, e.g. Porsche, BMW or Tuk Tuk"}

             {:db/ident :car/model
              :db/valueType :db.type/string
              :db/caridnality :db.cardinality/one
              :db/doc "Particular version or design e.g. Tycan Turbo S"}

             {:db/ident :car/year
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc "Production year e.g. 2020"}

             {:db/ident :car/vin
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "Vehicle identification number, 
                       a 17-digit chassis number"}

             {:db/ident :customer/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "name of user"}

             {:db/iden :customer/id
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "Users identification e.g. srz123"}

             {:db/ident :customer/cars
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many
              :db/doc "List of cars user owns"}])



(def data [{:car/make "Porsche"
            :car/model "Tycan Turbo S"
            :car/year 2020
            :car/vin "SNTFW1CT9EKF16180"}
           
           {:car/make "Porsche"
            :car/model "911 Turbo S"
            :car/year 2020
            :car/vin "1HVBBABN1YH370317"}
           
           {:car/make "Alfa Romeo"
            :car/model "4C"
            :car/year 2020
            :car/vin "5N1AR2MMXEC750672"}
           
           {:car/make "Alpine"
            :car/model "A110S"
            :car/year 2019
            :car/vin "JT3HN86R3V0013510"}

           {:car/make "Lotus"
            :car/model "Evora GT"
            :car/year 2018
            :car/vin "2HGFA1F53AH302980"}
           
           {:car/make "BMW"
            :car/model "M2"
            :car/year 2019
            :car/vin "JM1BK343781807968"}

           {:car/make "Mazda"
            :car/model "Miata MX-5"
            :car/year 2019
            :car/vin "WAUBL88G2RA045400"}

           {:car/make "Caterham"
            :car/model "Super Seven 1600"
            :car/year 2018
            :car/vin "5TFRM5F10AX081471"}

           {:car/make "Toyota"
            :car/model "GT 86 Coupe"
            :car/year 2012
            :car/vin "JF1GE6B65AH595022"}])



(def types [{:db/ident :coupe}
            {:db/ident :limousine}
            {:db/ident :estate}])


(def db-url (str "datomic:mem://" (d/squuid)))

(defn init-db
  "Connect to the database and transact schema"
  []
  (if (d/create-database db-url)

    (-> db-url
        (d/connect)
        (d/transact {:tx-data schema}))))
