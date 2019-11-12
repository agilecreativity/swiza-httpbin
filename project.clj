(defproject net.b12n/swiza-httpbin "0.1.0"
  :description "swiza-httpbin"
  :url "http://github.com/agilecreativity/swiza-httpbin"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljfmt "0.6.1"]
            [jonase/eastwood "0.3.5"]
            [lein-auto "0.1.3"]
            [lein-cloverage "1.0.13"]
            [lein-binplus "0.6.5"]
            [lein-hiera "1.1.0"]
            [io.taylorwood/lein-native-image "0.3.1"]]
  :bin {:name "httpbin"
        :bin-path "~/bin"
        :bootclasspath false}
  :native-image
  {:graal-bin :env/GRAALVM_HOME
   :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
   :opts ["-H:EnableURLProtocols=http"
          "--report-unsupported-elements-at-runtime"
          "--initialize-at-build-time"
          "--no-fallback"
          #=(str "--rerun-class-initialization-at-runtime="
                 #=(clojure.string/join ","
                                        ["javax.net.ssl.SslContext"
                                         "org.httpkit.client.SslContextFactory"
                                         "org.httpkit.client.HttpClient"
                                         "sun.security.ssl.SSLContextImpl$DefaultSSLContext"]))
          "--enable-url-protocols=http,https"
          "--enable-http"
          "--enable-https"
          "--enable-all-security-services"
          "-H:+ReportExceptionStackTraces"
          "--no-server"
          "--verbose"]
   :name "httpbin-graal"}
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :test-paths ["src/test/clojure"
               "src/test/java"]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [akvo/fs "20180904-152732.6dad3934"]
                 [cli-matic "0.3.8"]
                 [http-kit "2.3.0"]
                 [metosin/jsonista "0.2.4"]
                 [camel-snake-kebab "0.4.0"]
                 [net.b12n/swiza-commons "0.1.2"]
                 [camel-snake-kebab "0.4.0"]]
  :main b12n.swiza.httpbin.core-utils
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]
                                  [jonase/eastwood "0.3.6"]
                                  [cucl "0.1.11"]
                                  [alembic "0.3.2"]
                                  [circleci/circleci.test "0.4.2"]
                                  [org.clojure/tools.namespace "0.2.11"]]
                   :global-vars {*warn-on-reflection* false
                                 *assert* false}}
             :uberjar {:aot :all}
             :1.9  {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :1.10 {:dependencies [[org.clojure/clojure "1.10.1"]]}}
  :aliases {"lint" ["do" ["cljfmt" "check"] ["eastwood"]]
            "test-all" ["with-profile" "default:+1.9:+1.10" "test"]
            "lint-and-test-all" ["do" ["lint"] ["test-all"]]
            "test"   ["run" "-m" "circleci.test/dir" :project/test-paths]
            "tests"  ["run" "-m" "circleci.test"]
            "retest" ["run" "-m" "circleci.test.retest"]})
