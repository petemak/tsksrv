(defproject tsksrv "0.1.0-SNAPSHOT"
  :description "Task service backend"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring "1.8.1"]
                 [conf-er "1.0.1"]
                 [integrant "0.8.0"]
                 [integrant/repl "0.3.1"]
                 [metosin/reitit "0.5.5"]
                 [com.datomic/datomic-free "0.9.5697"]]
  :main ^:skip-aot tsksrv.server
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[midje "1.9.9"]
                                  [clj-http "3.10.2"]]
                   :plugins [[lein-midje "3.2.2"]]}
             :uberjar {:aot :all}}
  :jvm-opts ["-Dconfig=system_config.edn"])
