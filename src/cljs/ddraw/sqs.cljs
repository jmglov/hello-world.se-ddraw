(ns ddraw.sqs
  (:require [cljsjs.aws-sdk-js]
            [ddraw.aws :as aws]))

(def receive-params)

(defn init! []
  (println "Initialising SQS")
  (js/AWS.SQS. (clj->js {:apiVersion "2012-11-05"})))

(defn create [sqs q-name on-created-fn]
  (println "Creating queue...")
  (.createQueue sqs (clj->js {:QueueName q-name})
                (aws/response-handler #(on-created-fn (.-QueueUrl %)))))

(defn get-arn [sqs queue-url arn-fn]
  (.getQueueAttributes sqs (clj->js {:QueueUrl queue-url
                                     :AttributeNames ["QueueArn"]})
                       (aws/response-handler #(arn-fn (-> % .-Attributes .-QueueArn)))))

(defn receive
  ([sqs queue-url message-fn]
   (receive sqs queue-url message-fn aws/error-handler))
  ([sqs queue-url message-fn err-fn]
   (println "Receiving message...")
   (.receiveMessage sqs (clj->js {:AttributeNames ["SentTimestamp"]
                                  :MaxNumberOfMessages 1
                                  :MessageAttributeNames ["All"]
                                  :QueueUrl queue-url
                                  :VisibilityTimeout 2
                                  :WaitTimeSeconds 0})
                    (aws/response-handler (fn [data]
                                            (if (and (.-Messages data) (pos? (count (.-Messages data))))
                                              (let [msg (aget data "Messages" 0)
                                                    delete-params {:QueueUrl queue-url
                                                                   :ReceiptHandle (.-ReceiptHandle msg)}]
                                                (message-fn (.-Body msg))
                                                (.deleteMessage sqs (clj->js delete-params)
                                                                (aws/response-handler (fn [& _] (println "Message deleted")))))
                                              (println "No message received")))
                                          err-fn))))

(defn set-policy [sqs queue-url policy]
  (let [policy {:Version "2012-10-17"
                :Statement [policy]}]
    (.setQueueAttributes sqs (clj->js {:QueueUrl queue-url
                                       :Attributes {:Policy (.stringify js/JSON (clj->js policy))}})
                         (aws/response-handler (fn [& _] (println "Set policy"))))))
