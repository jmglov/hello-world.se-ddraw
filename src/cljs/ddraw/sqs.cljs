(ns ddraw.sqs
  (:require [cljsjs.aws-sdk-js]
            [ddraw.aws :as aws]))

(def receive-params)

(defn init! []
  (println "Initialising SQS")
  (js/AWS.SQS. (clj->js {:apiVersion "2012-11-05"})))

(defn create [sqs q-name on-created-fn]
  (println "Creating queue" q-name)

  ;; Do stuff here

  )

(defn get-arn [sqs queue-url arn-fn]
  (println "Getting queue ARN for" queue-url)

  ;; Do stuff here
  )

(defn receive
  ([sqs queue-url message-fn]
   (receive sqs queue-url message-fn aws/error-handler))
  ([sqs queue-url message-fn err-fn]
   (println "Receiving message from queue" queue-url)

   ;; Do stuff here
   ))

(defn set-policy [sqs queue-url policy]
  (println "Setting policy for queue" queue-url)
  (let [policy {:Version "2012-10-17"
                :Statement [policy]}]

    ;; Do stuff here
    ))
