(ns ddraw.views
  (:require [ddraw.events :as events]
            [ddraw.subs :as subs]
            [re-frame.core :as rf]))

(defn reset-to-element-value [atom element]
  (reset! atom (-> element .-target .-value)))

(defn main-panel []
  (let [authenticated? (rf/subscribe [::subs/authenticated?])
        queue-created? (rf/subscribe [::subs/queue-created?])]
    (if @authenticated?
      (do
        (when-not @queue-created?
          (rf/dispatch [::events/create-queue!]))
        [:div "Ready to roll!"])
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
