(ns ddraw.widgets
  (:require [cljs.tools.reader :refer [read-string]]
            [ddraw.events :as events]
            [ddraw.shapes :as shapes]
            [ddraw.subs :as subs]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn get-value [element]
  (-> element .-target .-value))

(def colors
  ["aquamarine"
   "mediumvioletred"
   "palevioletred"
   "lime"
   "darkgoldenrod"
   "sandybrown"
   "sienna"
   "orange"
   "mediumturquoise"
   "rosybrown"
   "yellowgreen"
   "aliceblue"
   "orangered"
   "seashell"
   "deepskyblue"
   "limegreen"
   "deeppink"
   "blueviolet"
   "darkorange"
   "gray"
   "seagreen"
   "lightpink"
   "skyblue"
   "greenyellow"
   "tan"
   "darkslategray"
   "darkslategrey"
   "lightsalmon"
   "bisque"
   "white"
   "midnightblue"
   "peachpuff"
   "lavenderblush"
   "slategray"
   "dimgrey"
   "paleturquoise"
   "darkorchid"
   "crimson"
   "antiquewhite"
   "chocolate"
   "lightslategrey"
   "darkseagreen"
   "lightgray"
   "yellow"
   "floralwhite"
   "mediumpurple"
   "indianred"
   "mediumspringgreen"
   "navy"
   "oldlace"
   "green"
   "indigo"
   "mediumslateblue"
   "cyan"
   "saddlebrown"
   "burlywood"
   "violet"
   "lightcoral"
   "darkolivegreen"
   "springgreen"
   "whitesmoke"
   "darkred"
   "ivory"
   "salmon"
   "slategrey"
   "honeydew"
   "darkviolet"
   "peru"
   "cornsilk"
   "darkgray"
   "navajowhite"
   "mistyrose"
   "gold"
   "gainsboro"
   "chartreuse"
   "lemonchiffon"
   "snow"
   "moccasin"
   "aqua"
   "darkgrey"
   "dodgerblue"
   "dimgray"
   "wheat"
   "hotpink"
   "lightgoldenrodyellow"
   "lightsteelblue"
   "lightgrey"
   "turquoise"
   "royalblue"
   "red"
   "blue"
   "mintcream"
   "mediumblue"
   "khaki"
   "maroon"
   "rebeccapurple"
   "cornflowerblue"
   "darkmagenta"
   "lightslategray"
   "darkgreen"
   "azure"
   "mediumorchid"
   "fuchsia"
   "firebrick"
   "coral"
   "darkblue"
   "orchid"
   "plum"
   "pink"
   "teal"
   "mediumseagreen"
   "lawngreen"
   "magenta"
   "forestgreen"
   "lightgreen"
   "darkcyan"
   "darkturquoise"
   "lightblue"
   "slateblue"
   "powderblue"
   "purple"
   "olivedrab"
   "ghostwhite"
   "steelblue"
   "goldenrod"
   "cadetblue"
   "palegreen"
   "thistle"
   "lightskyblue"
   "blanchedalmond"
   "lightcyan"
   "silver"
   "grey"
   "darkslateblue"
   "darksalmon"
   "brown"
   "olive"
   "tomato"
   "linen"
   "mediumaquamarine"
   "lavender"
   "papayawhip"
   "palegoldenrod"
   "darkkhaki"
   "beige"
   "black"
   "lightseagreen"
   "lightyellow"])

(defn button
  ([on-click-fn label]
   (button {} on-click-fn label))
  ([attrs on-click-fn label]
   [:span {:style {:background-color "pink"
                   :margin 5}} (str label " button")]))

(defn color-picker []
  (let [on-change-fn #(rf/dispatch [::events/assoc-shape :color (get-value %)])]
    [:span {:style {:background-color "pink"}} "color picker"]))

(defn num-input [k]
  (let [on-change-fn #(rf/dispatch [::events/assoc-shape k (read-string (get-value %))])]
    [:span {:style {:background-color "pink"}} "input"]))

(defn span [style text]
  [:span {:style style} text])

(defn login-form []
  (let [username (r/atom nil)
        password (r/atom nil)]
    [:div {:style {:display "flex"
                   :width 250
                   :height 250
                   :background-color "grey"}}
     [:span {:style {:width "100%"
                     :text-align "center"
                     :font-size 24
                     :margin "auto"}}
      "Login form"]]))

(defn dispatch-shape [shape]
  (rf/dispatch [::events/add-shape shape])
  (rf/dispatch [::events/publish-command shape])
  (rf/dispatch [::events/input-shape nil]))

(defn circle-input []
  (let [shape (rf/subscribe [::subs/current-shape])
        on-click-add-fn #(let [{:keys [x y radius color]} @shape]
                           (dispatch-shape (shapes/circle [x y] radius color)))]
    [:div {:style {:display "flex"}}
     [:span {:style {:background-color "pink"}} "circle input"]]))

(defn rectangle-input []
  (let [shape (rf/subscribe [::subs/current-shape])
        on-click-add-fn #(let [{:keys [x y width height color]} @shape]
                           (dispatch-shape (shapes/rectangle [x y] width height color)))]
    [:div {:style {:display "flex"}}
     [:span {:style {:background-color "pink"}} "rectangle input"]]))

(defn triangle-input []
  (let [shape (rf/subscribe [::subs/current-shape])
        on-click-add-fn #(let [{:keys [x1 y1 x2 y2 x3 y3 color]} @shape]
                           (dispatch-shape (shapes/triangle [x1 y1] [x2 y2] [x3 y3] color)))]
    [:div {:style {:display "flex"}}
     [:span {:style {:background-color "pink"}} "triangle input"]]))

(defn text-input []
  (let [shape (rf/subscribe [::subs/current-shape])
        on-text-change-fn #(rf/dispatch [::events/assoc-shape :text (get-value %)])
        on-click-add-fn #(let [{:keys [x y size color text]} @shape]
                           (dispatch-shape (shapes/text [x y] size color text)))]
    [:div {:style {:display "flex"}}
     [:span {:style {:background-color "pink"}} "text input"]]))
