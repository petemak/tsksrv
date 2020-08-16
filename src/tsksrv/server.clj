(ns tsksrv.server
  (:require [tsksrv.routes :as routes]
            ;; For reading configuration
            [conf-er :as conf]
            ;; Ring HHTP adapter
            [ring.adapter.jetty :as jetty]))

(defonce srv (atom nil))


;;-----------------------------------------------
;; Application starter.
;;
;; Uses a ring adapter
;; Adapters convert Ring handlers into running web servers.
;; 
;; :port - the port to listen on (defaults to 80)
;; :join? - blocks the thread if true
;; https://ring-clojure.github.io/ring/ring.adapter.jetty.html
;;-----------------------------------------------
(defn start!
  "Starts the server on a port defined in the
  configuration properties :server-port attribute"
  []
  (let [port (conf/config :port)
        jetty (jetty/run-jetty (routes/handler {:conn ""
                                                :db "NOT AVAILABLE"})
                               {:port port
                                :join? false})]
    (reset! srv jetty)
    (println "Jetty started on port [" port "]")))

;;-----------------------------------------------
;; Stop server
;;
;; Stops server without the need to kill REPL
;; 
;;-----------------------------------------------
(defn stop!
  "Stops instance of the running server
   and frees the port without the need to kill
   the REPL"
  []
  (if-let [port (conf/config :port)]
    (do
      (println "Shutting down Jetty on port [" port "]")
      (.stop @srv))))



