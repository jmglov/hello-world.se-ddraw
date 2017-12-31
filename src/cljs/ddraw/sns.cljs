(ns ddraw.sns
  (:require [cljsjs.aws-sdk-js]))

(def receive-params {:AttributeNames ["SentTimestamp"]
                     :MaxNumberOfMessages 1
                     :MessageAttributeNames ["All"]
                     :VisibilityTimeout 2
                     :WaitTimeSeconds 0})

(defn init! []
  (println "Initialising SNS")
  (js/AWS.SNS. (clj->js {:apiVersion "2012-11-05"})))

(defn publish [sns sns-topic msg on-publish-fn]
  (.publish sns (clj->js {:TopicArn sns-topic
                          :Message msg})
            (fn [err data]
              (if err
                (println "Error publishing:" (.-message err))
                (on-publish-fn)))))

(defn subscribe! [sns sns-topic queue-arn on-subscribed-fn]
  (.subscribe sns (clj->js {:Protocol "sqs"
                            :TopicArn sns-topic
                            :Endpoint queue-arn})
              (fn [err data]
                (if err
                  (println "Error subscribing:" (.-message err))
                  (on-subscribed-fn)))))
