(ns b12n.swiza.httpbin.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [b12n.swiza.commons.core-utils :refer [load-edn-config]]
            [org.httpkit.client :as http]
            [clojure.walk :refer [postwalk]]
            [camel-snake-kebab.core :refer [->camelCaseString ->kebab-case-keyword]]
            [jsonista.core :as j :refer [object-mapper read-value]]))

(defn ^:private process-response [body]
  (j/read-value body
                (j/object-mapper {:decode-key-fn ->kebab-case-keyword})))

(defn http-request
  "Generic http request function"
  [& [{:keys [url
              method
              content-type
              accept
              path]
       :or {url "https://httpbin.org"
            method :get
            content-type :json
            accept :json}}]]
  (let [api-key "some-api-key"]
    (let [response
          (http/request
           {:url (format "%s/%s" url path)
            :headers {"Authorization" (format "APIKey %s" api-key)}
            :content-type :json
            :accept :json
            :method method})
          {:keys [opts body headers status] :as resp} @response]
      (if (contains? #{200 201} status)
        (if (= content-type :json)
          (j/read-value body (j/object-mapper {:decode-key-fn ->kebab-case-keyword}))
          body)
        status))))

(comment

  ;; Sample POST request
  (http-request {:path "post"
                 :method :post})

  ;; See: http://httpbin.org/#/Response_formats
  (->
   (http-request {:path "json"})
   pprint
   )

  #_
  {:slideshow
   {:date "date of publication",
    :slides
    [{:type "all", :title "Wake up to WonderWidgets!"}
     {:type "all",
      :title "Overview",
      :items
      ["Why <em>WonderWidgets</em> are great"
       "Who <em>buys</em> WonderWidgets"]}],
    :title "Sample Slide Show",
    :author "Yours Truly"}}

  (-> (http-request {:path "gzip"})
      pprint
      )
  #_
  {:method "GET",
   :headers
   {:user-agent "http-kit/2.0",
    :authorization "APIKey some-api-key",
    :host "httpbin.org",
    :accept-encoding "gzip, deflate"},
   :gzipped true,
   :origin "103.55.134.61, 103.55.134.61"}

  (http-request {:path "html"
                 :content-type "text/html"
                 :accept "text/html"})

  (->
   (http-request {:path "encoding/utf8"
                  :content-type "text/html"
                  :accept "text/html"})
   println
   )

  (->
   (http-request {:path "xml"
                  :content-type "application/xml"
                  :accept "application/xml"})
   println
   )

  (http-request {:path "ip"})
  #_{:origin "71.178.14.108, 71.178.14.108"}

  (http-request {:path "user-agent"})
  #_{:user-agent "http-kit/2.0"}

  (http-request {:path "headers"})
  #_{:headers {:user-agent "http-kit/2.0", :authorization "APIKey some-api-key", :host "httpbin.org", :accept-encoding "gzip, deflate"}}

  (->
   (http-request {:path "get"})
   pprint
   )

  #_
  {:args {},
   :headers
   {:user-agent "http-kit/2.0",
    :authorization "APIKey some-api-key",
    :host "httpbin.org",
    :accept-encoding "gzip, deflate"},
   :url "https://httpbin.org/get",
   :origin "103.55.134.61, 103.55.134.61"}

  (http-request {:path (format "base64/%s" "SFRUUEJJTiBpcyBhd2Vzb21l")
                 :content-type "text/html"
                 :accept "text/html"})

  (http-request {:path "bytes/20"
                 :content-type "application/octet-stream"
                 :accept "application/octet-stream"})

  (http-request {:path "delay/2"
                 :content-type :json
                 :accept :json})

  (http-request {:path "links/3/3"
                 :content-type :html
                 :accept :html})

  (http-request {:path "uuid"})
  ;;=> {:uuid "ac24dddc-fdeb-4d53-9965-b80e32712a54"}

  (http-request {:path "image"
                 :content-type "image/webp"
                 :accept "image/webp"})

  (http-request {:path "image/jpeg"
                 :content-type "image/jpeg"
                 :accept "image/jpeg"})

  (http-request {:path "image/jpeg"
                 :content-type "image/jpeg"
                 :accept "image/jpeg"})

  (http-request {:path "image/png"
                 :content-type "image/png"
                 :accept "image/png"})

  (->
   (http-request {:path "image/svg"
                  :content-type "image/svg+xml"
                  :accept "image/svg+xml"})
   println)
  )

(defn -main [& args]
  ;; for sunec native lib loading at native-image runtime
  (System/setProperty "java.library.path" (str (System/getenv "GRAALVM_HOME") "/jre/lib"))

  (let [result (http-request {:path "json"})]
    (pprint result)))

(comment
  ;; scratch area
  (-main) ;;=> see your REPL or console

  #_
  {:slideshow
   {:date "date of publication",
    :slides
    [{:type "all", :title "Wake up to WonderWidgets!"}
     {:type "all",
      :title "Overview",
      :items
      ["Why <em>WonderWidgets</em> are great"
       "Who <em>buys</em> WonderWidgets"]}],
    :title "Sample Slide Show",
    :author "Yours Truly"}}
  )
