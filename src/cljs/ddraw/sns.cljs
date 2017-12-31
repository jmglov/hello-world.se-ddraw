(ns ddraw.sns
  (:require [cljsjs.aws-sdk-js]
            [ddraw.aws :as aws]))

(defn init! []
  (println "Initialising SNS")
  (js/AWS.SNS. (clj->js {:apiVersion "2012-11-05"})))

(defn publish [sns sns-topic msg on-publish-fn]
  (.publish sns (clj->js {:TopicArn sns-topic
                          :Message msg})
            (aws/response-handler on-publish-fn)))

(defn subscribe! [sns sns-topic queue-arn on-subscribed-fn]
  (.subscribe sns (clj->js {:Protocol "sqs"
                            :TopicArn sns-topic
                            :Endpoint queue-arn})
              (aws/response-handler on-subscribed-fn)))
