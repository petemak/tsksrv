(ns tsksrv.server
  (:require [tsksrv.handler :as h]
            [reitit.core :as r]
            [reitit.ring :as ring]
            ;; swagger spec handler and UI
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            ;; Muuntaja content negotation and encoding/decoding
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            ;; Coercion
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            ;; Exception handling
            [reitit.ring.middleware.exception :as exception]
            ;; Ring HHTP adapter
            [ring.adapter.jetty :as jetty]))


;;-----------------------------------------------
;; Create swagger UI handler for visualizing
;; the swagger spec.
;;
;; To serve the swagger-ui there is:
;;
;; reitit.swagger-ui/create-swagger-ui-hander
;;
;; It creates a ring-handler to serve the
;; Swagger-ui, a user interface to visualize
;; and interact with the Swagger specification.
;; https://cljdoc.org/d/metosin/reitit/0.5.5/doc/ring/swagger-support
;;-----------------------------------------------
(def swagger-ui-handler
  (swagger-ui/create-swagger-ui-handler {:path "/"}))

;;-----------------------------------------------
;; Creates a ring handler for serving swagger spec
;; i.e. /swagger.json routes
;;
;; To serve Swagger Specification, there is
;;
;; reitit.swagger/create-swagger-handler.
;;
;; It returns a ring-handler which, at request-time,
;; collects data from all routes for the same swagger api
;; and returns a formatted Swagger specification as Clojure data,
;; to be encoded by a response formatter.
;;-----------------------------------------------
(def swagger-handler
  (swagger/create-swagger-handler))


;;-----------------------------------------------
;; HTTP route definition.
;; Routes are defined as vectors of
;; string path and optional route argument child.
;;
;; Swagger
;; "/swagger.json" route served with a swagger-handler
;;                 created with (swagger/create-swagger-handler)
;;
;; Coersion
;; We define parameter and response coersion for each
;; request type.
;; {:get {:parameters {:path {:id string?}}
;;        :response {:body string?}}}
;;  
;; 
;;-----------------------------------------------
(def routes
  [["/ping" {:get {:swagger {:tags ["Service Health"]}
                   :handler h/ok}}]
   ["/swagger.json" {:get {:no-doc true
                           :swagger {:info  {:title "Task Management API"
                                             :description "REST API for managing tasks"}}
                           :handler swagger-handler}}]
   ["/api" {:swagger {:tags ["Service Apis"]}}
    ["/tasks" {:get {:response {200 {:body string?}}
                     :handler h/ok}}]
    ["/tasks/:id" {:get {:parameters {:path {:id string?}}
                         :response {:body string?}
                         :handler h/ok}
                   :post {:parameters {:path {:id string?}
                                       :body {:name string?
                                              :description string?
                                              :state int?}}
                          :response {200 {:body string?}}
                          :handler h/ok}} ]]])



;;-----------------------------------------------
;; Create routing function
;; 
;; Using ring-router which adds support for 
;; ring concepts like:
;; -> middleware
;; -> handler
;;
;; Coercion:
;; Muuntaja middleware is injected using
;; the :muuntaja and :coersion kewords
;;
;; Exception handling
;; Middleware added before the coercing middleware
;; and before format-request in order to catch
;; coercing errors
;;-----------------------------------------------
(def router
  (ring/router routes
               {:data {:coercion reitit.coercion.spec/coercion
                       :muuntaja   m/instance
                       :middleware [;; muuntaja/format-middleware
                                    muuntaja/format-negotiate-middleware
                                    muuntaja/format-response-middleware
                                    exception/exception-middleware
                                    muuntaja/format-request-middleware
                                    coercion/coerce-request-middleware
                                    coercion/coerce-response-middleware]}}))




;;-----------------------------------------------
;; Create ring handler
;; Adds support for snychronous and asynchronous
;; request handling
;; Takes
;; - the ring router created using (ring/router routes)
;; - Optional default handler here swagger-ui ring/routes
;;-----------------------------------------------
(def app
  (ring/ring-handler router
                     (ring/routes swagger-ui-handler)))


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
