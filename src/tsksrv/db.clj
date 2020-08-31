(ns tsksrv.db
  (:require [datomic.api :as d]))



;;-----------------------------------------------
;; Task schema: describes atribiutes that make
;; up a task.
;;-----------------------------------------------
(def task-schema [{:db/ident :task/name
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "The name of the task"}

                  {:db/ident :task/tid
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "Unique task identifier"}
                  
                  {:db/ident :task/description
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "The task description"}

                  {:db/ident :task/creation-date
                   :db/valueType :db.type/instant
                   :db/cardinality :db.cardinality/one
                   :db/doc "The date on which the task was created"}
                  
                  {:db/ident :task/status
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "The status of a task"}])


;;-----------------------------------------------
;; Datalog query for all tasks
;;-----------------------------------------------
(def all-tasks-querry
  '[:find ?e ?tid ?name ?descr ?crd
    :where [?e :task/tid ?tid]
           [?e :task/name ?name]
           [?e :task/description ?descr]
           [?e :task/creation-date ?crd]])

;;-----------------------------------------------
;; Utililty functions
;;-----------------------------------------------
(defn unique-id!
  "Generate a unique identifier"
  []
  (.toString (java.util.UUID/randomUUID)))

;; ----------------------------------------------
;; create-databas: Creates database specified by uri.
;;                 Returns true if the database was
;;                 created, false if exists.
;;
;; connect: Connects to the specified database,
;;          returing a Connection. 
(defn connect
  "Connect to the data base URI
   and returns the connection"
  [uri]
  (if (d/create-database uri)
    (d/connect uri)))

;; ----------------------------------------------
;; Transact: Submits a transaction to the database
;; for writing.
;;
;; ----------------------------------------------
(defn start-db!
  "Connect to the database specified by the uri
   and transacts the schema"
  [uri]
  (let [conn (connect uri)
        db (d/transact conn task-schema)]
    {:uri uri
     :conn conn
     :db db}))


;; ----------------------------------------------
;; This method should be called as
;; part of clean shutdown of a JVM process.
;;
;; ----------------------------------------------
(defn stop-db!
  "Shut down database resources"
  []
  (d/shutdown false))


;; ----------------------------------------------
;; Save a task. 
;; - name and description must be provided
;; - id and creation date will be generated
;; - returns list of datoms in key :tx-data
;;
;; Transact retunrs a map containing 4 keys
;; :db-before database value before the transaction
;; :db-after  database value after the transaction
;; :tx-data   collection of Datoms produced by the transaction
;; :tempids   argument to resolve-tempids
;;
;; {:db-before {:database-id "58a47389-f1ab-4d81-85b6-715cecde9bac", 
;;              :t 1000, 
;;              :next-t 1001, 
;;              :history false}, 
;;  :db-after {:database-id "58a47389-f1ab-4d81-85b6-715cecde9bac", 
;;             :t 1001, 
;;             :next-t 1005, 
;;             :history false}, 
;;  :tx-data [ #datom[13194139534317 50 #inst "2017-02-15T19:28:52.270-00:00" 
;;                                                          13194139534317 true] 
;;             #datom[17592186045422 63 "The Goonies" 13194139534317 true] 
;;             #datom[17592186045422 64 "action/adventure" 13194139534317 true] 
;;             #datom[17592186045422 65 1985 13194139534317 true] 
;;             #datom[17592186045423 63 "Commando" 13194139534317 true] 
;;             #datom[17592186045423 64 "action/adventure" 13194139534317 true] 
;;             #datom[17592186045423 65 1985 13194139534317 true] 
;;             #datom[17592186045424 63 "Repo Man" 13194139534317 true] 
;;             #datom[17592186045424 64 "punk dystopia" 13194139534317 true] 
;;             #datom[17592186045424 65 1984 13194139534317 true]], 
;;  :tempids {-9223301668109598138 17592186045422, -9223301668109598137 17592186045423, 
;;                                                 -9223301668109598136 17592186045424}}
;; ----------------------------------------------
(defn save-task
  "Save task with specified name n, description d,
   and s status"
  [{:keys [conn name description]}]
  (let [data [{:task/tid (unique-id!)
               :task/name name
               :task/description description
               :task/creation-date (java.util.Date.)
               :task/status "open"}]]
    (println "::-> Conn: " conn ", name" name ", " description)
    @(d/transact conn data)))


;; ----------------------------------------------
;; Rerieve all tasks from the databse
;; ----------------------------------------------
(defn load-tasks
  "Use the connection ro retrieve all tasks from
  te database"
  [{:keys [conn]}]
  (d/q all-tasks-querry (d/db conn)))
