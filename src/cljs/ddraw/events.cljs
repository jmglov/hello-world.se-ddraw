(ns ddraw.events
  (:require [ddraw.cognito :as cognito]
            [ddraw.config :as config]
            [ddraw.db :as db]
            [ddraw.sns :as sns]
            [ddraw.sqs :as sqs]
            [re-frame.core :as rf]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 ::authenticated
 (fn [db _]
   (assoc db
          :sqs (sqs/init!)
          :sns (sns/init!))))

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
   (sqs/create sqs (str "ddraw-client-" (random-uuid))
               #(rf/dispatch-sync [::queue-created %]))
   db))

(rf/reg-event-db
 ::queue-created
 (fn [{:keys [sqs] :as db} [_ q]]
   (println "Queue" q "created")
   (sqs/get-arn sqs q #(rf/dispatch-sync [::queue-arn-read %]))
   (assoc db :sqs-q q)))

(rf/reg-event-db
 ::queue-arn-read
 (fn [{:keys [sns sqs sqs-q] :as db} [_ q-arn]]
   (sns/subscribe! sns config/sns-topic q-arn
                   #(println "Queue" sqs-q "subscribed to topic" config/sns-topic))
   (sqs/set-policy sqs sqs-q (-> config/sqs-policy
                                 (assoc "Resource" q-arn)
                                 (assoc-in ["Condition" "ArnEquals" "aws:SourceArn"] config/sns-topic)))
   (assoc db :sqs-q-arn q-arn)))

(rf/reg-event-db
 ::receive-message
 (fn [{:keys [sqs sqs-q] :as db} _]
   (sqs/receive sqs sqs-q)
   db))
