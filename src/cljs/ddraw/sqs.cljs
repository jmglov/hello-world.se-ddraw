(ns ddraw.sqs
  (:require [cljsjs.aws-sdk-js]))

(def queue-url "https://sqs.eu-west-1.amazonaws.com/166399666252/ddraw.fifo")
(def receive-params {:AttributeNames ["SentTimestamp"]
                     :MaxNumberOfMessages 1
                     :MessageAttributeNames ["All"]
                     :QueueUrl queue-url
                     :VisibilityTimeout 2
                     :WaitTimeSeconds 0})

(defn init! []
  (println "Initialising SQS")
  (js/AWS.SQS. (clj->js {:apiVersion "2012-11-05"})))

(defn receive [q]
  (println "Receiving message...")
  (.receiveMessage q (clj->js receive-params)
                   (fn [err data]
                     (cond
                       err
                       (println "Error receiving message:" err)

                       (and (.-Messages data) (pos? (count (.-Messages data))))
                       (let [msg (aget data "Messages" 0)
                             delete-params {:QueueUrl queue-url
                                            :ReceiptHandle (.-ReceiptHandle msg)}]
                         (println "Message body" (.-Body msg))
                         (println "Receipt handle:" (:ReceiptHandle delete-params))
                         (.deleteMessage q (clj->js delete-params)
                                         (fn [err data]
                                           (if err
                                             (println "Delete error:" err)
                                             (println "Message deleted")))))

                       :default
                       (println "No message received")))))
