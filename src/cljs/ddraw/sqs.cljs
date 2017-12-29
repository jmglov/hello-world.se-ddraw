(ns ddraw.sqs
  (:require [cljsjs.aws-sdk-js]))

(def receive-params {:AttributeNames ["SentTimestamp"]
                     :MaxNumberOfMessages 1
                     :MessageAttributeNames ["All"]
                     :VisibilityTimeout 2
                     :WaitTimeSeconds 0})

(defn init! []
  (println "Initialising SQS")
  (js/AWS.SQS. (clj->js {:apiVersion "2012-11-05"})))

(defn create [sqs q-name on-created-fn]
  (println "Creating queue...")
  (.createQueue sqs (clj->js {:QueueName q-name})
                (fn [err data]
                  (if err
                    (println "Error creating queue:" (.-message err))
                    (on-created-fn (.-QueueUrl data))))))

(defn receive [sqs queue-url]
  (println "Receiving message...")
  (.receiveMessage sqs (clj->js (assoc receive-params :QueueUrl queue-url))
                   (fn [err data]
                     (cond
                       err
                       (println "Error receiving message:" (.-message err))

                       (and (.-Messages data) (pos? (count (.-Messages data))))
                       (let [msg (aget data "Messages" 0)
                             delete-params {:QueueUrl queue-url
                                            :ReceiptHandle (.-ReceiptHandle msg)}]
                         (println "Message body" (.-Body msg))
                         (println "Receipt handle:" (:ReceiptHandle delete-params))
                         (.deleteMessage sqs (clj->js delete-params)
                                         (fn [err data]
                                           (if err
                                             (println "Delete error:" (.-message err))
                                             (println "Message deleted")))))

                       :default
                       (println "No message received")))))
