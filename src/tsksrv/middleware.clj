(ns tsksrv.middleware
  (:require [reitit.core :as r]))
;;------------------------------------------
;; Database middleware. Add db reference
;; to request map for later use by handlers
;; Must be placed right after content
;; negotiation and coercion middleware
;;
;; Note: defined as first-class data entity
;;------------------------------------------
(def db
  {:name ::dbm
   :compile (fn [{:keys [db]} opts]
              (fn [handler]
                (fn [request]
                  (handler (assoc request :db db)))))})
