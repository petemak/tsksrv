(ns tsksrv.handler)

(defn ok
  [req]
  {:status 200
   :body (str "Path:" (:path req) ", path params:" (:path-params req))})

