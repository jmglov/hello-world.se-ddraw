(ns ddraw.views
  (:require [cljs.tools.reader :refer [read-string]]
            [ddraw.events :as events]
            [ddraw.subs :as subs]
            [re-frame.core :as rf]))

(defn reset-to-element-value [atom element]
  (reset! atom (-> element .-target .-value)))

(defn main-panel []
  (let [authenticated? (rf/subscribe [::subs/authenticated?])
        queue-created? (rf/subscribe [::subs/queue-created?])
        latest-id (rf/subscribe [::subs/latest-id])]
    (if @authenticated?
      (do
        (if @queue-created?
          [:div
           (str "Latest ID: " @latest-id)
           [:button {:on-click #(rf/dispatch-sync [::events/receive-message
                                                   (fn [msg]
                                                     (when-let [{:keys [id]} (->> msg
                                                                                  (.parse js/JSON)
                                                                                  .-Message
                                                                                  read-string)]
                                                       (rf/dispatch-sync [::events/set-id id])))])}
            "Process queue"]]
          (do
            (rf/dispatch [::events/create-queue!])
            [:div "Creating queue"])))
      (let [username (atom nil)
            password (atom nil)]
        [:div
         "Username:" [:input {:type "text"
                              :on-change #(reset-to-element-value username %)}]
         "Password:" [:input {:type "password"
                              :on-change #(reset-to-element-value password %)}]
         [:button {:on-click #(do
                                (println "Logging in as" @username "/" @password)
                                (rf/dispatch-sync [::events/login! @username @password]))}
          "Login"]]))))
