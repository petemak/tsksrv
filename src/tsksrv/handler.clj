(ns tsksrv.handler)


(defn ping
  [req]
  {:status 200
   :body "0.0.1"})

(defn ok
  [{:keys [path path-params db] :as req}]
  {:status 200
   :body (str "Path:" path
              ", path params:" path-params
              ", db:" db)})
