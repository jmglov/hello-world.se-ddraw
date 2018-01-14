(ns ddraw.sns
  (:require [cljsjs.aws-sdk-js]
            [ddraw.aws :as aws]))

(defn init! []
  (println "Initialising SNS")
  (js/AWS.SNS. (clj->js {:apiVersion "2012-11-05"})))

(defn publish [sns sns-topic msg on-publish-fn]
  (println "Publishing message to SNS topic" sns-topic ":" msg)

  ;; Do stuff here

  )

(defn subscribe! [sns sns-topic queue-arn on-subscribed-fn]
  (println "Subscribing queue" queue-arn "to SNS topic" sns-topic)

  ;; Do stuff here

  )
