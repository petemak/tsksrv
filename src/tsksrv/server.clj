(ns tsksrv.server
  (:require [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.adapter.jetty :as jetty]))


;;-----------------------------------------------
;; Create swagger UI handler for use as default handler
;;
;;-----------------------------------------------
(def default-swagger-ui-handler
  (ring/routes (swagger-ui/create-swagger-ui-handler {:path "/"})))

;;-----------------------------------------------
;; Creates a ring handler for serving /swagger.json routes
;; It generated the swagger-ui.
;;-----------------------------------------------
(defn swagger-ui-handler
  [req]
  (let [handler (swagger/create-swagger-handler)]
    (handler req )))


;;-----------------------------------------------
;; HTTP route definition.
;; Routes are defined as vectors of
;; string path and optional route argument child.
;;-----------------------------------------------
(def routes
  [["/ping" {:get (fn [req] {:status 200 :body "ok"} )}]
   ["/swagger.json" {:get {:handler swagger-ui-handler}}]
   ["/api" {:route "api"}
    ["/tasks" {:a 2}]
    ["/tasks/:id" {:a 3}]]])



;;-----------------------------------------------
;; Create routing function
;; 
;; Using ring-router which adds support for 
;; ring concepts like:
;; -> request-method routing
;; -> middleware
;; -> handler
;;-----------------------------------------------
(def router
  (ring/router routes))




;;-----------------------------------------------
;; Create ring handler
;; Adds support for snychronous and asynchronous
;; request handling
;; Takes
;; - the ring router created using (ring/router routes)
;; - Optional default handler here swagger-ui
;;-----------------------------------------------
(def app
  (ring/ring-handler router default-swagger-ui-handler))


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
(defn start
  "Starts the server on a port defined in the
  configuration properties :server-port attribute"
  []
  (jetty/run-jetty #'app {:port 3449 :join? false})
  (println "Jetty started on port 3449"))


(defn stop
  []
  )


(comment
  ;; Show data associated with the paths
  (r/routes router)
  (r/match-by-path router "/api/tasks/123")

  (app {:request-method :get
        :uri "/ping"})
  )
