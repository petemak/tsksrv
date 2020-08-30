(ns tsksrv.db-test
  (:use [midje.sweet])
  (:require [tsksrv.db :as db]
            [conf-er :as conf]))



;; ----------------------------------------------------
;;  Manage databse connnection
;; ----------------------------------------------------
(defonce db-conn (atom 0))

(defn start-db!
  "Start db abd store reference to connection in atom"
  []
  (let [dbref (db/start-db! (str "datomic:mem://testdb" (rand-int 1000)))]
    (reset! db-conn dbref)))



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
  (db/stop-db!)
  (reset! db-conn nil))

(against-background [(before :contents  (start-db!))
                     (after :contents (stop-db!))]
 (fact "Adding data should "
   (let [res (db/save-task (-> @db-conn
                               (dissoc :db)
                               (assoc :name "test")
                               (assoc :description "Testing save-task")))]
     (some? (:tx-data res)) => true
     (count (:tx-data res)) => 6)) )

