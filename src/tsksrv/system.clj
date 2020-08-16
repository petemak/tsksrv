
(ns tsksrv.system
  (:require [conf-er :as conf]
            [tsksrv.db :as db]
            [tsksrv.routes :as routes]
            ;; Integrant for managing component dependencies
            [integrant.core :as ig]
            ;; Ring HHTP adapter
            [ring.adapter.jetty :as jetty]))

;; ------------------------------------------------------
;; Configuration map.
;; Each top-level key in the map represents
;; a configuration that can be "initialized"
;; into a concrete implementation.
;; Configurations can reference other keys via the ref
;;
;; adapter -> handler -> Db -> config
;; ------------------------------------------------------
(def system-config
  {:adapter/jetty        {:cfg     (ig/ref :config/properties)
                          :handler  (ig/ref :handler/ring-handler)}
   :handler/ring-handler {:db  (ig/ref :db/datomic-free)}
   :db/datomic-free      {:cfg (ig/ref :config/properties)}
   :config/properties nil})

;; ------------------------------------------------------
;; ig/init-key tells Integrant needs to implement the system map.
;; The init-key multimethod takes two arguments
;; 1) a key and 
;; 2) its corresponding value
;;
;; and tells Integrant how to initialize it.
;;
;; First element in the list will the component itself (top-level key)
;; and the next argument the map as defined in the confing
;; ------------------------------------------------------

;; ------------------------------------------------------
;; HTTP adapter :adapter/jetty
;; ------------------------------------------------------
(defmethod ig/init-key :adapter/jetty [a {:keys [cfg handler]}]
  (if-let [port (get cfg :port)]
    (do
      (println "::-> tsksrv.system - Initialising " a " with cfg "
               cfg "and handler" handler)
      (jetty/run-jetty handler {:port port :join? false}))
    (println "::-> tsksrv.system - Initialisation of " a "failed. 
              Port not found")))

;; ------------------------------------------------------
;; Handler :handler/ring-handler
;;
;; ------------------------------------------------------
(defmethod ig/init-key :handler/ring-handler [h {:keys [db]}]
  (println "::-> tsksrv.system - Initialising " h " with db" db)
  (routes/handler db))

;; ------------------------------------------------------
;; DB component :db/datomic-free
;; Initialises datomic DB instance
;; ------------------------------------------------------
(defmethod ig/init-key :db/datomic-free [d {:keys [cfg]}]
  (if-let [db-uri (get cfg :db-uri)]
    (do
      (println "::-> tsksrv.system - Initialising " d " 
                     with db-uri" db-uri)
      (db/init-db db-uri))
    (println "::-> tsksrv.system - Initialising " d " 
                   failed. db-uri not found")))

;; ------------------------------------------------------
;; Configuration :config/properties 
;; ------------------------------------------------------
(defmethod ig/init-key :config/properties [c _]
  (println "::-> tsksrv.system - Initialising " c)
  {:port (conf/config :port)
   :db-uri (conf/config :db-uri)})

;;--------------------------------------------------
;; Shut downs
;;--------------------------------------------------
(defmethod ig/halt-key! :adapter/jetty [_ jetty]
  (println "::-> tsksrv.system - halting " jetty)
  (.stop jetty))

(defmethod ig/halt-key! :handler/ring-handler [_ h]
  (println "::-> tsksrv.system - nothing to halt for " h)  )


(defmethod ig/halt-key! :db/datomic-free [_ d]
  (println "::-> tsksrv.system - halting " d)
  (db/shutdown))


(defmethod ig/halt-key! :config/properties [_ c]
  (println "::-> tsksrv.system - nothing to halt for" c)  )

(comment
  (def system (ig/init system-config))
  (ig/halt! system)
  
 )
