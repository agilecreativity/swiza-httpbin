(ns b12n.swiza.httpbin.core-utils
  (:require [b12n.swiza.httpbin.core :refer [http-request]]
            [clojure.pprint :refer [pprint]]
            [cli-matic.core :refer [run-cmd]])
  (:gen-class))

(def shared-opts
  "Common options for GET/POST/DELETE/PUT call"
  [{:option "config"
    :as "config"
    :short "c"
    :type :string
    :default "./config.edn"}])

(defn call-get-cmd
  [{:keys [config]}]
  (-> (http-request {:path "gzip"
                     :method :get})
      pprint))

(defn call-post-cmd
  [{:keys [config]}]
  (->
   (http-request {:path "post" :method :post})
   pprint))

(defn call-put-cmd
  [{:keys [config]}]
  (println "PUT : " config))

(defn call-delete-cmd
  [{:keys [config]}]
  (println "POST : " config))

(def get-cmd {:command "get-cmd"
              :description ["Call http GET end-point"]
              :opts shared-opts
              :runs call-get-cmd})

(def post-cmd {:command "post-cmd"
               :description ["Call http POST end-point"]
               :opts shared-opts
               :runs call-post-cmd})

(def put-cmd {:command "put-cmd"
              :description ["Call http PUT end-point"]
              :opts shared-opts
              :runs call-put-cmd})

(def delete-cmd {:command "put-cmd"
                 :description ["Call http DELETE end-point"]
                 :opts shared-opts
                 :runs call-delete-cmd})

;; Configuration section
(def CONFIGURATION
  {:app {:command "http-api"
         :description "Rest wrapper via cli-matic/graalvm"
         :version "0.1.0"}
   :global-opts [{:option "config"
                  :as "Configuration file"
                  :type :string
                  :default "config.edn"}]
   :commands [get-cmd
              post-cmd
              put-cmd
              delete-cmd]})

(defn -main [& args]
  ;; for sunec native lib loading at native-image runtime
  (System/setProperty "java.library.path" (str (System/getenv "GRAALVM_HOME") "/jre/lib"))
  (run-cmd args CONFIGURATION))

;; Try the following from the terminal
;; $lein run           ## to get the help
;; $lein run post-cmd  ## to hit sample http-post
;; $lein run get-cmd   ## to hit sample http-get
