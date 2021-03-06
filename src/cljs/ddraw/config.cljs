(ns ddraw.config)

(def debug?
  ^boolean goog.DEBUG)

(def cognito
  {:user-pool-id "eu-west-1_RXytVgDNt"
   :client-id "1p2tpv6ats7b22eepd3c808ift"
   :identity-pool-id "eu-west-1:666dd9df-bebf-451b-9c3c-fc8bdeb43832"})

(def sns-topic "arn:aws:sns:eu-west-1:166399666252:ddraw")

(def sqs-policy
  {"Sid" "SnsPublish"
   "Effect" "Allow"
   "Principal" "*"
   "Action" "sqs:SendMessage"
   "Resource" "QUEUE-ARN"
   "Condition" {"ArnEquals" {"aws:SourceArn" "SNS-ARN"}}})
