(ns ddraw.views
  (:require [cljs.tools.reader :refer [read-string]]
            [ddraw.colors :as colors]
            [ddraw.events :as events]
            [ddraw.shapes :as shapes]
            [ddraw.subs :as subs]
            [re-frame.core :as rf]))

(defn reset-to-element-value
  ([atom element]
   (reset-to-element-value atom element identity))
  ([atom element f]
   (reset! atom (f (-> element .-target .-value)))))

(defn color-picker [color-atom]
  [:select
   {:value @color-atom
    :on-change #(reset-to-element-value color-atom %)}
   (map (fn [c] [:option {:key c} (name c)]) (keys colors/color))])

(defn num-input [num-atom]
  [:input {:type "text"
           :size 1
           :on-change #(reset-to-element-value num-atom % read-string)}])

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

             [:button {:on-click #(rf/dispatch-sync [::events/publish-shape :clear])}
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
                :rectangle (let [x (atom nil)
                                 y (atom nil)
                                 width (atom nil)
                                 height (atom nil)
                                 color (atom (first (keys colors/color)))]
                             [:div
                              "x" (num-input x)
                              "y" (num-input y)
                              "width" (num-input width)
                              "height" (num-input height)
                              "color" (color-picker color)
                              [:button {:on-click #(let [shape (shapes/rectangle [@x @y] @width @height (keyword @color))]
                                                     (rf/dispatch-sync [::events/publish-shape shape]))} "Add"]])
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
