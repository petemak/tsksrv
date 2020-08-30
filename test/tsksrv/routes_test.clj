(ns tsksrv.routes-test
  (:use [midje.sweet])
  (:require [conf-er :as conf]
            [clojure.string :as st]
            [tsksrv.routes :as routes]))

(fact "All roots  (ping, api..)  must return status 200 OK"
      (let [png-req {:request-method :get
                     :uri "/ping"}
            tsk-req {:request-method :get
                     :uri "/api/tasks"}
            tskid-req {:request-method :get
                         :uri "/api/tasks/task-101"}
            app (routes/handler {})]
        (:status (app png-req))  => 200
        (:status (app tsk-req)) => 200
        (:status (app tskid-req))  => 200))

(fact "version route must return version as specified"
      (let [api-ver (conf/config :service-version)
            tsk-req {:request-method :get
                     :uri "/ping"}
            app (routes/handler {})]
        ;; (:wrap (app task-req)) => '(:api)
        (:body (app tsk-req)) => api-ver))

(fact "route must return a user map
       containing id, first and last "
      (let [tskid-req {:request-method :get
                       :uri "/api/tasks/task-101"}
            tskid-pos {:request-method :post
                       :body-params {:name "Bugs Bunny"
                                    :description "The rabit of Seville"}
                       :uri "/api/tasks/user-101"}
            app (routes/handler {})]
        ;; (:wrap (app tskid-req)) => '(:api :admin)
        ;; (:wrap (app usr-pos)) => '(:api :admin)
        (some? (:body (app tskid-req))) => true
        (st/starts-with? (:body (app tskid-req)) "Path:") => true))
