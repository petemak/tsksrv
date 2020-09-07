(ns tsksrv.movies
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
(def db-uri "datomic:free://localhost:4334/movies")


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
(def schema [{:db/ident :movie/title
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
              :db/doc "The year the movie was released in theaters"}])

(def movies [{:movie/title "Alien"
              :movie/genre "action/adventure"
              :movie/release-year 1979}
             
             {:movie/title "Mad Max"
              :movie/genre "action/adventure"
              :movie/release-year 1979}
             
             {:movie/title "Repo Man"
              :movie/genre "punk dystopia"
              :movie/release-year 1984}
                 
             {:movie/title "Commando"
              :movie/genre "action/adventure"
              :movie/release-year 1985}
                 
             {:movie/title "Predator"
              :movie/genre "action/adventure"
              :movie/release-year 1987}])


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
(def res-data (d/transact conn movies))

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
(def allmovies (d/q '[:find ?e ?t
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



