(ns ddraw.events
  (:require [ddraw.db :as db]
            [ddraw.sqs :as sqs]
            [re-frame.core :as rf]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-event-db
 ::init-sqs
 (fn  [db _]
   (assoc db :sqs-q (sqs/init!))))

(rf/reg-event-db
 ::receive-message
 (fn  [{:keys [sqs-q] :as db} _]
   (sqs/receive sqs-q)
   db))
