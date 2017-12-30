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

(defn get-arn [sqs queue-url arn-fn]
  (.getQueueAttributes sqs (clj->js {:QueueUrl queue-url
                                     :AttributeNames ["QueueArn"]})
                       (fn [err data]
                         (if err
                           (println "Error getting queue ARN:" (.-message err))
                           (arn-fn (-> data .-Attributes .-QueueArn))))))

(defn receive
  ([sqs queue-url message-fn]
   (receive sqs queue-url message-fn #(println "Error receiving message:" (.-message %))))
  ([sqs queue-url message-fn err-fn]
   (println "Receiving message...")
   (.receiveMessage sqs (clj->js (assoc receive-params :QueueUrl queue-url))
                    (fn [err data]
                      (cond
                        err
                        (err-fn err)

                        (and (.-Messages data) (pos? (count (.-Messages data))))
                        (let [msg (aget data "Messages" 0)
                              delete-params {:QueueUrl queue-url
                                             :ReceiptHandle (.-ReceiptHandle msg)}]
                          (message-fn (.-Body msg))
                          (.deleteMessage sqs (clj->js delete-params)
                                          (fn [err data]
                                            (if err
                                              (println "Delete error:" (.-message err))
                                              (println "Message deleted")))))

                        :default
                        (println "No message received"))))))

(defn set-policy [sqs queue-url policy]
  (let [policy {:Version "2012-10-17"
                :Statement [policy]}]
    (.setQueueAttributes sqs (clj->js {:QueueUrl queue-url
                                       :Attributes {:Policy (.stringify js/JSON (clj->js policy))}})
                         (fn [err data]
                           (if err
                             (println "Error setting policy:" (.-message err))
                             (println "Set policy"))))))
