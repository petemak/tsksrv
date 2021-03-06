(ns tsksrv.datomic-dev
  (:require [datomic.api :as d]))

;; ---------------------------------------------
;; Assumes transactor started
;; $ ./bin/transactor ./config/transactor.properties
;; Launching with Java options -server -Xms1g -Xmx1g -XX:+UseG1GC -XX:Ma...s=50
;; Starting datomic:dev://localhost:4334/<DB-NAME>, storing data in: data ...
;; System started datomic:dev://localhost:4334/<DB-NAME>, storing data in: data
;; ---------------------------------------------

;;----------------------------------------------
;; 1. DB uri
;; Note we are creating a new uri EACH TIME
;; In memory URI fomart: "datomic:mem://<name>"
;;----------------------------------------------
  (def db-uri "datomic:free://localhost:4334/test")


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
;; 1. Movie with four attributes title, genre and release-year
;; 2. customer/owner
;; -----------------------------------------------
(def movie-schema [{:db/ident :movie/title
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The title of the movie"}

                   {:db/ident :movie/genre
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The genre of the movie"}

                   {:db/ident :movie/release-year
                    :db/valueType :db.type/long
                    :db/cardinality :db.cardinality/one
                    :db/doc "The year the movie was released in theaters"}

                   {:db/ident :movie/director
                    :db/valueType :db.type/ref
                    :db/cardinality :db.cardinality/many
                    :db/doc "controls a film's artistic and dramatic
                             aspects and visualizes the screenplay"}

                   {:db/ident :movie/sequel
                    :db/valueType :db.type/ref
                    :db/cardinality :db.cardinality/one
                    :db/doc "Continuation of the story or  earlier work"}
 
                   {:db/ident :movie/cast
                    :db/valueType :db.type/ref
                    :db/cardinality :db.cardinality/many
                    :db/doc "List of actors"}

                   {:db/ident :person/name
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "Full name of a person"}

                   {:db/ident :person/born
                    :db/valueType :db.type/instant
                    :db/cardinality :db.cardinality/one
                    :db/doc "Persons date of birth"}])

(def movie-data [{:db/id -214
                  :movie/title "Alien"
                  :movie/year 1979
                  :movie/director -137
                  :movie/cast [-138 -139 -140]
                  :movie/sequel -215}

                 {:db/id -216
                  :movie/title "Mad Max"
                  :movie/year 1979
                  :movie/director [-142]
                  :movie/cast [-112 -143 -144]
                  :movie/sequel -217}
                 
                 {:db/id -205
                  :movie/title "Commando"
                  :movie/genre "action/adventure"
                  :movie/release-year 1985
                  :movie/director [-119]
                  :movie/cast [-101 -120 -121]}
                 
                 {:db/id -202
                  :movie/title "Predator"
                  :movie/year 1987
                  :movie/director [-108]
                  :movie/cast [-101 -109 -110]}

                 {:db/id -203
                  :movie/title "Lethal Weapon"
                  :movie/year 1987
                  :movie/director [-111]
                  :movie/cast [-112 -113 -114]}


                 {:db/id -101
                  :person/name "Arnold Schwarzenegger"
                  :person/born #inst "1947-07-30"}
                

                 {:db/id -108
                   :person/name "John McTiernan"
                  :person/born #inst "1951-01-08"}
                 
                 {:db/id -109
                  :person/name "Elpidia Carrillo"
                  :person/born #inst "1961-08-16"}
                 

                 {:db/id -110
                  :person/name "Carl Weathers"
                  :person/born #inst "1948-01-14"}

                 {:db/id -111
                  :person/name "Richard Donner"
                  :person/born #inst "1930-04-24"}

                 {:db/id -112
                  :person/name "Mel Gibson"
                  :person/born #inst "1956-01-03"}

                 {:db/id -113
                  :person/name "Danny Glover"
                  :person/born #inst "1946-07-22"}

                 {:db/id -114
                  :person/name "Gary Busey"
                  :person/born #inst "1944-07-29"}

                 {:db/id -115
                  :person/name "Paul Verhoeven"
                  :person/born #inst "1938-07-18"}

                 {:db/id -119
                  :person/name "Mark L. Lester"
                  :person/born #inst "1946-11-26"}

                 {:db/id -120
                  :person/name "Rae Dawn Chong"
                  :person/born #inst "1961-02-28"}

                 {:db/id -121
                  :person/name "Alyssa Milano"
                  :person/born #inst "1972-12-19"}

                 ])


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
(def res-schema (d/transact conn movie-schema))



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
(def res-data (d/transact conn movie-data))

;;----------------------------------------------
;; 6. Point in time database value
;;
;; The value is used for querrying
;;----------------------------------------------
(def db (d/db conn))


;;----------------------------------------------
;; 7. Querry movies
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
(def movies (d/q '[:find ?e ?t
                   :where
                   [?e :movie/title ?t]]
                 db))

;;----------------------------------------------
;; 8. Querry movies titles for 1985
;; Datalog query in EDN
;;
;;----------------------------------------------
(def movies1985 (d/q '[:find ?t
                       :where
                       [?e :movie/title ?t]
                       [?e :movie/release-year 1985]]
                     db))


;;----------------------------------------------
;; 8. Querry movies titles for 1985
;; Datalog query in EDN
;;
;;----------------------------------------------
(def movies1985attr (d/q '[:find ?e ?t ?g ?y
                           :where
                           [?e :movie/title ?t]
                           [?e :movie/release-year 1985]
                           [?e :movie/genre ?g]
                           [?e :movie/release-year ?y]]
                     db))



