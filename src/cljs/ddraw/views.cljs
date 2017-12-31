(ns ddraw.views
  (:require [ddraw.events :as events]
            [ddraw.shapes :as shapes]
            [ddraw.subs :as subs]
            [re-frame.core :as rf]))

(defn reset-to-element-value [atom element]
  (reset! atom (-> element .-target .-value)))

(defn main-panel []
  (let [authenticated? (rf/subscribe [::subs/authenticated?])
        listening? (rf/subscribe [::subs/listening?])
        queue-created? (rf/subscribe [::subs/queue-created?])
        shapes (rf/subscribe [::subs/shapes])
        shape-input (rf/subscribe [::subs/shape-input])]
    (if @authenticated?
      (do
        [:div
         [:svg {:width 640
                :height 480}
          (shapes/rectangle [0 0] 640 480 :light-gray)
          (->> @shapes
               (map-indexed (fn [i [shape attrs]]
                              [shape (assoc attrs :key i)])))]
         (if @queue-created?
           [:div
            [:div
             (if @listening?
               [:button {:on-click #(rf/dispatch-sync [::events/stop-listening])}
                "Stop processing queue"]
               [:button {:on-click #(rf/dispatch-sync [::events/start-listening])}
                "Start processing queue"])

             [:button {:on-click #(rf/dispatch-sync [::events/clear-shapes])}
              "Clear shapes"]]
            [:div
             [:button {:on-click #(rf/dispatch-sync [::events/new-shape :rectangle])}
              "Rectangle"]
             [:button {:on-click #(rf/dispatch-sync [::events/new-shape :circle])}
              "Circle"]
             [:button {:on-click #(rf/dispatch-sync [::events/new-shape :triangle])}
              "Triangle"]
             [:button {:on-click #(rf/dispatch-sync [::events/new-shape :text])}
              "Text"]]
            (when @shape-input
              (case @shape-input
                :rectangle [:div "Rectangle inputs"]
                :circle [:div "Circle inputs"]
                :triangle [:div "Triangle inputs"]
                :text [:div "Text inputs"]))]
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
