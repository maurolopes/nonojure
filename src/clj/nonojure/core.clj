(ns nonojure.core
  (:require
   [compojure.core :refer :all]
   [compojure.handler :as chandler]
   [compojure.route :as croute]
   [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
   [ring.util.response :refer [response]]
   [org.httpkit.server :as httpkit]
   [ring.util.response :refer [file-response]]
   [cheshire.core :as json]
   [nonojure
    [db :as db]
    [config :refer [config]]
    [user :as user]]
   [taoensso.timbre :as timbre
    :refer [trace debug info warn error fatal spy with-log-level]]))

(defn- parse-filter-value [value]
  (if value
    (->> (clojure.string/split value #"-")
         (map #(Double/parseDouble %)))
    nil))

(defroutes api
  (GET "/nonograms" [filter value sort order]
       (response (db/find-nonograms {:filter-field (keyword filter)
                                     :filter-value (parse-filter-value value)
                                     :sort-field (keyword sort)
                                     :sort-order (keyword order)})))
  (GET "/nonograms/:id" [id]
       (response (db/find-nonogram-by-id id)))
  (ANY "/echo" req
       (response (pr-str req)))
  (POST "/user/login" req (user/login req))
  (context "/user" [] (user/wrap-restricted user/user-api))
  (POST "/rate/:id" [id difficulty]
        (response (db/update-difficulty id (Integer/parseInt difficulty)))))

(defn wrap-error [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           (println e)
           (.printStackTrace e)
           {:status 500}))))

(defroutes app-routes
  (GET "/api-examples" [] (file-response "resources/api-examples.html"))
  (GET "/rating" [] (file-response "resources/rating.html"))
  (context "/api" [] (-> api
                         (wrap-error)
                         (wrap-json-body {:keywords? true})
                         (wrap-json-response {:pretty true})))
  (croute/resources "/static")
  (GET "*" [] (file-response "resources/index.html"))
  (croute/not-found "Nothing to see here, move along."))

(defn wrap-logging [handler]
  (fn [req]
    (let [resp (handler req)]
      (debug (str "reg: " (:uri req) " " (:status resp)))
      resp)))

(def app
  (let [cookie-attrs {:max-age (get-in config [:web :cookie-max-age])
                      :http-only true}]
      (-> #'app-routes
          (chandler/site {:session {:cookie-attrs cookie-attrs}})
          (wrap-logging))))

(defn start []
  (db/connect (:mongo config))
  (let [stop (httpkit/run-server #'app (:web config))]
    (info (str "Started server on port " (get-in config [:web :port])))
    stop))

#_(
   (def server (start))

   (server))
