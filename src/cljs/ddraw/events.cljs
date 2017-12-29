(ns ddraw.events
  (:require [ddraw.cognito :as cognito]
            [ddraw.config :as config]
            [ddraw.db :as db]
            [ddraw.sqs :as sqs]
            [re-frame.core :as rf]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 ::authenticated
 (fn [db _]
   (assoc db :sqs (sqs/init!))))

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
   (assoc db :sqs-q q)))

(rf/reg-event-db
 ::receive-message
 (fn [{:keys [sqs sqs-q] :as db} _]
   (sqs/receive sqs sqs-q)
   db))
