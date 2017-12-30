(ns ddraw.views
  (:require [ddraw.events :as events]
            [ddraw.subs :as subs]
            [re-frame.core :as rf]))

(defn reset-to-element-value [atom element]
  (reset! atom (-> element .-target .-value)))

(defn main-panel []
  (let [authenticated? (rf/subscribe [::subs/authenticated?])
        listening? (rf/subscribe [::subs/listening?])
        queue-created? (rf/subscribe [::subs/queue-created?])
        shapes (rf/subscribe [::subs/shapes])]
    (if @authenticated?
      (do
        [:div
         [:svg {:width 640
                :height 480}
          [:rect {:style {:stroke-width 0}
                  :x 0, :y 0
                  :width 640, :height 480
                  :fill "#bbbbbb"}]
          (->> @shapes
               reverse
               (map-indexed (fn [i [shape attrs]]
                              [shape (assoc attrs :key i)])))]
         (if @queue-created?
           [:div
            (if @listening?
              [:button {:on-click #(rf/dispatch-sync [::events/stop-listening])}
               "Stop processing queue"]
              [:button {:on-click #(rf/dispatch-sync [::events/start-listening])}
               "Start processing queue"])]
           (do
             (rf/dispatch [::events/create-queue!])
             [:div "Creating queue"]))])
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
