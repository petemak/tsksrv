(defproject tsksrv "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring "1.8.1"]
                 [conf-er "1.0.1"]
                 [integrant "0.8.0"]
                 [integrant/repl "0.3.1"]
                 [metosin/reitit "0.5.5"]
                 [midje "1.9.9"]]
  :main ^:skip-aot tsksrv.server
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
