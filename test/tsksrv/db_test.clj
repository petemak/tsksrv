(ns tsksrv.db-test
  (:use [midje.sweet])
  (:require [tsksrv.db :as db]
            [conf-er :as conf]
            [datomic.api :as d]))



;; ----------------------------------------------------
;;  Manage databse connnection
;; ----------------------------------------------------
(defonce db-conn (atom 0))

(defn start-db!
  "Start db abd store reference to connection in atom"
  []
  (let [uri (str "datomic:mem://" (d/squuid))]
    (d/delete-database uri)
    (reset! db-conn (db/start-db! uri))))



;; ----------------------------------------------------
;; The web service
;;  1) can be started using (start-server)
;;  2) and stopped using (stop-server)
;;
;; This gives us the opportunity to use
;; the service in an integration test
;
;; We use the "against-background" macro from Midje to:
;;  1) fire up the service before tests
;;  2) shut it down after tghe tests
;;
;; We can therefore do a real HTTP call
;; ----------------------------------------------------
(defn stop-db!
  "Close connection and clear atom"
  []
  (if-let [conn  (:conn @db-conn)]
    (d/delete-database (:uri @db-conn)))
  (db/stop-db!)
  (reset! db-conn nil))


(against-background [(before :contents  (start-db!))
                     (after :contents (stop-db!))]
    (fact "Intialising the database should add  "
       (let [res (db/save-task (-> @db-conn
                                   (dissoc :db)
                                   (assoc :name "test")
                                   (assoc :description "Testing save-task")))]
         (some? (:tx-data res)) => true
         (count (:tx-data res)) => 6
         (count (:tempids res)) => 1)))
