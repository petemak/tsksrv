(ns tsksrv.datomic
  (:require [datomic.api :as d]))


;; -----------------------------------------------
;;
;; In Datomic, we describe entities each by their
;; attributes:
;;
;; Every attribute definition has three required attributes:
;;
;; :db/ident     - name for your attribute
;; :db/valueType - specifies the type of data that can be
;;                 stored in the attribute
;; :db/cardinality  - specifies whether the attribute stores
;;                    a single value, or a collection of values
;; :db/doc attribute - stores a docstring of the attribute
;;
;; Schema describes two entities
;; 1. car with four attributes make, model, year and 
;; 2. customer/owner
;; -----------------------------------------------
(def schema [{:db/ident :car/make
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "Brand of car, e.g. Porsche, BMW or Tuk Tuk"}

             {:db/ident :car/model
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
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

             {:db/ident :customer/id
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "Users identification e.g. srz123"}

             {:db/ident :customer/cars
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many
              :db/doc "List of cars user owns"}])



(def tdat [{:car/make "Porsche"
            :car/model "Tycan Turbo S"
            :car/year 2020
            :car/vin "SNTFW1CT9EKF16180"}
           
           {:car/make "Pagani"
            :car/model "Huayra"
            :car/year 2013
            :car/vin "3FAHP07118R192740"}
           
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

;;----------------------------------------------
;; 1. DB uri
;; Note we are creating a new uri EACH TIME
;; In memory URI fomart: "datomic:mem://<name>"
;;----------------------------------------------
(defonce db-uri (str "datomic://mem://" (d/squuid)))


;;----------------------------------------------
;; 2. Create database
;; Creates database specified by uri. Returns:
;; - true if the database was created
;; - false if it already exists.
;;----------------------------------------------

(def created (d/create-database db-uri))

;;----------------------------------------------
;; 3. Connect
;; Connects to the specified database
;; returing a Connection.
;;----------------------------------------------
(def conn (d/connect db-uri))

;;----------------------------------------------
;; 4. Transact schema
;;
;; Submit schema data to the database for writing.
;; The transaction data is sent to the Transactor and,
;; if transactAsync, processed asynchronously.
;;
;; Returns a completed future that can be used to
;; monitor the completion of the transaction.
;; If the transaction commits, the future's value is
;; a map containing the following keys:
;;
;;  :db-before         database value before the transaction
;;  :db-after          database value after the transaction
;;  :tx-data           collection of Datoms produced by the transaction
;;  :tempids           argument to resolve-tempids
;;----------------------------------------------
(def res-schema (d/transact conn schema))



;;----------------------------------------------
;; 5. Transact data
;;
;; Submit data to the database for writing.
;; The transaction data is sent to the Transactor and,
;; if transactAsync, processed asynchronously.
;;
;; Returns a completed future that can be used to
;; monitor the completion of the transaction.
;; If the transaction commits, the future's value is
;; a map containing the following keys:
;;
;;  :db-before         database value before the transaction
;;  :db-after          database value after the transaction
;;  :tx-data           collection of Datoms produced by the transaction
;;  :tempids           argument to resolve-tempids
;;----------------------------------------------
(defn res-data (d/transact conn tdat))


;;----------------------------------------------
;; 6. Point in time database value
;;
;; The value is used for querrying
;;----------------------------------------------
(def db (d/db conn))


;;----------------------------------------------
;; 7. Querry entity makes
;; Datalog query in EDN
;;
;; A query is a vector starting with the
;; - :find clause keyword :find followed by 
;;   pattern variables (symbols ?, e.g. ?e ?title).
;; - :where clause which restricts the query to
;;   datoms that match the given "data patterns".
;;
;; [<e-id>  <attribute>      <value>          <tx-id>]
;; [ 137    :car/make        "Porsche"        911  ]
;; [ 137    :car/model       "Carrera 911"    911  ]
;;----------------------------------------------
(def makes (d/q '[:find ?e ?m
                  :where [?e :car/make ?m]]
                db))



;;----------------------------------------------
;; 8. Query the entity id of the Tycan Turbo S
;; Find clause [:find ?2 .
;;
;; ex: 17592186045418
;;----------------------------------------------
(def eid-tycan (d/q '[:find ?e .
                     :where [?e :car/model "Tycan Turbo S"]]
                     db))

;;----------------------------------------------
;; 9. Entity navigation. Get all attributes
;; (d/entity <db> <e-id>
;;
;; ex: #:db{:id 17592186045418}
;;----------------------------------------------
(def ent-tycan (d/entity db eid-tycan))


;;----------------------------------------------
;; 10. Touch all of the attributes of the entity,
;; 
;; ex:
;; {:db/id 17592186045418
;;  :car/make "Porsche"
;;  :car/model "Tycan Turbo S"
;;  :car/year 2020
;;  :car/vin "SNTFW1CT9EKF16180"}
;;----------------------------------------------
(def ent-tycan-atrr (d/touch ent-tycan))




;;----------------------------------------------
;; 11. even the doc attribute is data
;;     Touch all of the attributes of the entity,
;
;;----------------------------------------------
(def doc-attr (d/entity db :db/doc))
(def doc-attr-attr (d/touch doc-attr))



