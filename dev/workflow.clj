(ns workflow
  (:require [tsksrv.system :as sys]
            [integrant.repl :as ig-repl]))


(ig-repl/set-prep! (fn [] sys/system-config))



(def start (ig-repl/go))
(def stop  (ig-repl/halt))
;;(def reset (ig-repl/reset))

  
