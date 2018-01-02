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
        current-shape (rf/subscribe [::subs/current-shape])
        toggle-input (fn [shape-type]
                       (rf/dispatch [::events/input-shape
                                     (if (= shape-type (:type @current-shape))
                                       nil
                                       shape-type)]))]
    (if @authenticated?
      (do
        [:div {:style {:width 640, :display "flex", :flex-direction "column", :justify-content "center"}}
         [:svg {:width 640
                :height 480}
          (shapes/rectangle [0 0] 640 480 "lightgray")
          (->> @shapes
               (map-indexed (fn [i [shape attrs & body]]
                              (vec (concat [shape (assoc attrs :key i)] body)))))]
         (if @queue-created?
           [:div
            [:div {:style {:display "flex", :margin-top 5}}
             [widgets/button {:style {:margin-left 0}} #(toggle-input :rectangle) "Rectangle"]
             [widgets/button {:style {:margin-left 5}} #(toggle-input :circle) "Circle"]
             [widgets/button {:style {:margin-left 5}} #(toggle-input :triangle) "Triangle"]
             [widgets/button {:style {:margin-left 5}} #(toggle-input :text) "Text"]
             [widgets/button {:style {:margin-left "auto"}}
              #(do
                 (rf/dispatch [::events/clear-shapes])
                 (rf/dispatch [::events/publish-command :clear])
                 (rf/dispatch [::events/input-shape nil]))
              "Clear shapes"]]
            (when @current-shape
              [:div {:style {:margin-top 5}}
               (case (:type @current-shape)
                 :circle [widgets/circle-input]
                 :rectangle [widgets/rectangle-input]
                 :triangle [widgets/triangle-input]
                 :text [widgets/text-input]
                 nil)])
            [:div {:style {:display "flex", :flex-direction "row-reverse", :margin-top 5}}
             (if @listening?
               [widgets/button #(rf/dispatch [::events/stop-listening]) "Stop processing queue"]
               [widgets/button #(rf/dispatch [::events/start-listening]) "Start processing queue"])]]
           (do
             (rf/dispatch [::events/create-queue!])
             [:div "Creating queue"]))])
      [widgets/login-form])))
