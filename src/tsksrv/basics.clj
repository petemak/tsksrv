(ns tsksrv.basics
  (:require [reitit.core :as r]))


;; ----------------------------------------------
;; Managed references
;; ----------------------------------------------
;; refs  - shared/synchronous/coordinated*
(defonce foo (ref {:name "Vivs" :age 7}))
(dosync
 (commute foo assoc :likes "Lemon drizzle"))

;; agents - shared/asynchronous*/autonomous
(defonce bah (agent {:name "Vivis" :age 7}))
(send bah assoc :likes "Lemon drizzle")

;; atom - shared/synchronous/autonomous
(defonce baz (atom {:name "Vivs" :age 7}))
(swap! baz update-in [:age] inc)


;; ----------------------------------------------
;; Code as data
;; ----------------------------------------------
(defn sum
  "Adds two "
  [args]
  (/ (apply + args) (count args)))


(def votes
  {:tom ["Butch" "Duff" "Baby" ]
   :jerry ["Spike" "Baby" "Topsy" ]
   :duff ["Tom" "Jerry"]})

(defn vote-count
  [vs]
  (-> vs
      vals
      flatten
      frequencies))



