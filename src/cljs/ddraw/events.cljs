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
            [goog.Timer]
            [re-frame.core :as rf]))

(defn get-id []
  (when-not (.getItem js/window.localStorage "id")
    (.setItem js/window.localStorage "id" (random-uuid)))
  (.getItem js/window.localStorage "id"))

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
            (rf/dispatch-sync [::add-shape command])))))))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 ::authenticated
 (fn [db _]
   (assoc db
          :sqs (sqs/init!)
          :sns (sns/init!)
          :authenticated? true)))

(rf/reg-event-db
 ::login!
 (fn [db [_ username password]]
   (let [{:keys [user-pool-id client-id identity-pool-id]} config/cognito]
     (println "Logging in as" username "/" password)
     (cognito/login! user-pool-id client-id identity-pool-id username password "_"
                     #(rf/dispatch-sync [::authenticated])))
   db))

(rf/reg-event-db
 ::create-queue!
 (fn [{:keys [sqs] :as db} _]
   (let [id (get-id)]
     (sqs/create sqs (str "ddraw-client-" id)
                 #(rf/dispatch-sync [::queue-created %]))
     (assoc db :id id))))

(rf/reg-event-db
 ::queue-created
 (fn [{:keys [sqs] :as db} [_ q]]
   (println "Queue" q "created")
   (sqs/get-arn sqs q #(rf/dispatch-sync [::queue-arn-read %]))
   (assoc db
          :sqs-q q
          :queue-created? true)))

(rf/reg-event-db
 ::queue-arn-read
 (fn [{:keys [sns sqs sqs-q] :as db} [_ q-arn]]
   (sns/subscribe! sns config/sns-topic q-arn
                   (fn [& _] (println "Queue" sqs-q "subscribed to topic" config/sns-topic)))
   (sqs/set-policy sqs sqs-q (-> config/sqs-policy
                                 (assoc "Resource" q-arn)
                                 (assoc-in ["Condition" "ArnEquals" "aws:SourceArn"] config/sns-topic)))
   (assoc db :sqs-q-arn q-arn)))

(rf/reg-event-db
 ::receive-message
 (fn [{:keys [sqs sqs-q] :as db} [_ message-fn]]
   (sqs/receive sqs sqs-q
                message-fn
                (fn [err]
                  (when (= "CredentialsError" (.-code err))
                    (rf/dispatch-sync [::reauth-required]))))
   db))

(rf/reg-event-db
 ::reauth-required
 (fn [db _]
   (assoc db :authenticated? false)))

(rf/reg-event-db
 ::add-shape
 (fn [db [_ shape]]
   (println "Adding shape:" shape)
   (update db :shapes conj shape)))

(rf/reg-event-db
 ::clear-shapes
 (fn [db _]
   (assoc db :shapes [])))

(rf/reg-event-db
 ::input-shape
 (fn [db [_ type]]
   (let [shape (merge {:type type, :color "black"}
                      (case type
                        :circle {:x 0, :y 0, :radius 0}
                        :rectangle {:x 0, :y 0, :width 0, :height 0}
                        :text {:x 0, :y 0, :size 14, :text ""}
                        :triangle {:x1 0, :y1 0, :x2 0, :y2 0, :x3 0, :y3 0}
                        nil))]
     (println "Setting current shape:" shape)
     (assoc db :current-shape shape))))

(rf/reg-event-db
 ::assoc-shape
 (fn [db [_ k v]]
   (assoc-in db [:current-shape k] v)))

(rf/reg-event-db
 ::publish-command
 (fn [{:keys [sns id] :as db} [_ command]]
   (let [msg (pr-str {:id id, :command command})]
     (sns/publish sns config/sns-topic msg (fn [& _] (println "Published:" msg))))
   db))

(rf/reg-event-db
 ::start-listening
 (fn [{:keys [id] :as db} _]
   (let [timer (goog.Timer. 5000)]
     (.start timer)
     (goog.events/listen timer goog.Timer/TICK
                         #(rf/dispatch-sync [::receive-message (partial handle-message id)]))
     (assoc db
            :listening? true
            :timer timer))))

(rf/reg-event-db
 ::stop-listening
 (fn [{:keys [timer] :as db} _]
   (.stop timer)
   (assoc db
          :listening? false
          :timer nil)))
