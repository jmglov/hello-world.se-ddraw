(ns ddraw.events
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [put! chan <! >! timeout close!]]
            [cljs.tools.reader :refer [read-string]]
            [ddraw.cognito :as cognito]
            [ddraw.config :as config]
            [ddraw.db :as db]
            [ddraw.sns :as sns]
            [ddraw.sqs :as sqs]
            [goog.events]
            [goog.Uri]
            [goog.Timer]
            [re-frame.core :as rf]))

(defn get-id-param []
  (-> js/document.location.href goog.Uri. (.getParameterValue "id")))

(defn get-id []
  (when-not (.getItem js/window.localStorage "id")
    (.setItem js/window.localStorage "id"
              (or (get-id-param) (random-uuid))))
  (or (get-id-param) (.getItem js/window.localStorage "id")))

(declare handle-message)

(defn receive-message [id]
  (rf/dispatch [::receive-message (partial handle-message id)]))

(defn handle-message [my-id msg]
  (let [{:keys [command id]} (->> msg
                                     (.parse js/JSON)
                                     .-Message
                                     read-string)]
    (if (nil? command)
      (println "Error: invalid message:" msg)
      (do
        (println "Received message:" msg)
        (if (= id my-id)
          (println "Skipping command from myself")
          (if (= :clear command)
            (rf/dispatch-sync [::clear-shapes])
            (rf/dispatch-sync [::add-shape command])))))
    (receive-message my-id)))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
