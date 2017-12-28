(ns ddraw.core
  (:require [cljsjs.aws-sdk-js]
            [cljsjs.amazon-cognito-identity-js]
            [ddraw.config :as config]
            [ddraw.events :as events]
            [ddraw.views :as views]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(enable-console-print!)

(def aws-region "eu-west-1")

(def authentication-data)

(def pool-data)

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (r/render [views/main-panel]
            (.getElementById js/document "app")))

(defn cognito-login! [user-pool-id client-id identity-pool-id username cur-password new-password]
  (aset js/AWSCognito.config "region" aws-region)
  (let [authentication-details (js/AWSCognito.CognitoIdentityServiceProvider.AuthenticationDetails.
                                (clj->js {:Username username
                                          :Password cur-password}))
        user-pool (js/AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool.
                   (clj->js {:UserPoolId user-pool-id
                             :ClientId client-id}))
        cognito-user (js/AWSCognito.CognitoIdentityServiceProvider.CognitoUser.
                      (clj->js {:Username username
                                :Pool user-pool}))]
    (println "Getting ready to authenticate")
    (.authenticateUser cognito-user authentication-details
                       (clj->js {:onSuccess
                                 (fn [res]
                                   (let [access-token (-> res .getAccessToken .getJwtToken)
                                         id-token (-> res .getIdToken .getJwtToken)]
                                     (println "Access token" access-token)
                                     (println "ID token" id-token)
                                     (aset js/AWS.config "region" aws-region)
                                     (aset js/AWS.config "credentials"
                                           (js/AWS.CognitoIdentityCredentials.
                                            (clj->js {:IdentityPoolId identity-pool-id
                                                      :Logins {(str "cognito-idp." aws-region
                                                                    ".amazonaws.com/" user-pool-id)
                                                               id-token}})))
                                     (println "Refreshing credentials")
                                     (.refresh js/AWS.config.credentials
                                               (fn [err]
                                                 (if err
                                                   (println "Error:" err))
                                                 (do
                                                   (rf/dispatch-sync [::events/init-sqs]))))))
                                 :onFailure
                                 (fn [err]
                                   (println "error" err))
                                 :newPasswordRequired
                                 (fn [user-attributes _]
                                   (println "new password required")
                                   (js-delete user-attributes "email_verified")
                                   (.completeNewPasswordChallenge cognito-user new-password
                                                                  user-attributes))}))))

(defn init-aws []
  (aset js/AWS.config "region" aws-region))

(defn ^:export init []
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root)
  (init-aws))
