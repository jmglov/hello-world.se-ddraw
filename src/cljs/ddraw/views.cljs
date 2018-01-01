(ns ddraw.views
  (:require [ddraw.events :as events]
            [ddraw.shapes :as shapes]
            [ddraw.subs :as subs]
            [ddraw.widgets :as widgets]
            [re-frame.core :as rf]
            [reagent.core :as r]))

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
          (shapes/rectangle [0 0] 640 480 "lightgray")
          (->> @shapes
               (map-indexed (fn [i [shape attrs]]
                              [shape (assoc attrs :key i)])))]
         (if @queue-created?
           [:div
            [:div
             (if @listening?
               (widgets/button #(rf/dispatch [::events/stop-listening]) "Stop processing queue")
               (widgets/button #(rf/dispatch [::events/start-listening]) "Start processing queue"))
             (widgets/button #(do
                                (rf/dispatch [::events/clear-shapes])
                                (rf/dispatch [::events/publish-shape :clear]))
                             "Clear shapes")]
            [:div
             (widgets/button #(rf/dispatch [::events/input-shape :rectangle]) "Rectangle")
             (widgets/button #(rf/dispatch [::events/input-shape :circle]) "Circle")
             (widgets/button #(rf/dispatch [::events/input-shape :triangle]) "Triangle")
             (widgets/button #(rf/dispatch [::events/input-shape :text]) "Text")]
            (when @shape-input
              (case @shape-input
                :rectangle (widgets/rectangle-input (fn [shape]
                                                      (rf/dispatch [::events/input-shape nil])
                                                      (rf/dispatch [::events/add-shape shape])
                                                      (rf/dispatch [::events/publish-shape shape])))
                :circle [:div "Circle inputs"]
                :triangle [:div "Triangle inputs"]
                :text [:div "Text inputs"]))]
           (do
             (rf/dispatch [::events/create-queue!])
             [:div "Creating queue"]))])
      (let [username (r/atom nil)
            password (r/atom nil)]
        [:div
         "Username:" (widgets/input :text username)
         "Password:" (widgets/input :password password)
         (widgets/button #(rf/dispatch [::events/login! @username @password]) "Login")]))))
