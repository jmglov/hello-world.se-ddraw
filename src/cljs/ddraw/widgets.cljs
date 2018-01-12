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
   [:button (merge attrs {:on-click on-click-fn}) label]))

(defn color-picker []
  [:select
   {:on-change #(rf/dispatch [::events/assoc-shape :color (get-value %)])}
   (map (fn [c] [:option {:key c} c]) colors)])

(defn num-input [k]
  [:input {:type "text"
           :placeholder "0"
           :size 1
           :on-change #(rf/dispatch [::events/assoc-shape k (read-string (get-value %))])}])

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
  (let [shape (rf/subscribe [::subs/current-shape])]
    [:div {:style {:display "flex"}}
     [span {:margin-right 5} "x"]
     [num-input :x]

     [span {:margin-left 5, :margin-right 5} "y"]
     [num-input :y]

     [span {:margin-left 5, :margin-right 5} "radius"]
     [num-input :radius]

     [span {:margin-left 5, :margin-right 5} "color"]
     [color-picker]

     [button {:style {:margin-left "auto"}}
      #(let [{:keys [x y radius color]} @shape]
         (dispatch-shape (shapes/circle [x y] radius color)))
      "Add"]]))

(defn rectangle-input []
  (let [shape (rf/subscribe [::subs/current-shape])]
    [:div {:style {:display "flex"}}
     [span {:margin-right 5} "x"]
     [num-input :x]

     [span {:margin-left 5, :margin-right 5} "y"]
     [num-input :y]

     [span {:margin-left 5, :margin-right 5} "width"]
     [num-input :width]

     [span {:margin-left 5, :margin-right 5} "height"]
     [num-input :height]

     [span {:margin-left 5, :margin-right 5} "color"]
     [color-picker]

     [button {:style {:margin-left "auto"}}
      #(let [{:keys [x y width height color]} @shape]
         (dispatch-shape (shapes/rectangle [x y] width height color)))
      "Add"]]))

(defn triangle-input []
  (let [shape (rf/subscribe [::subs/current-shape])]
    [:div {:style {:display "flex"}}
     [span {:margin-right 5} "x1"]
     [num-input :x1]

     [span {:margin-left 5, :margin-right 5} "y1"]
     [num-input :y1]

     [span {:margin-left 5, :margin-right 5} "x2"]
     [num-input :x2]

     [span {:margin-left 5, :margin-right 5} "y2"]
     [num-input :y2]

     [span {:margin-left 5, :margin-right 5} "x3"]
     [num-input :x3]

     [span {:margin-left 5, :margin-right 5} "y3"]
     [num-input :y3]

     [span {:margin-left 5, :margin-right 5} "color"]
     [color-picker]

     [button {:style {:margin-left "auto"}}
      #(let [{:keys [x1 y1 x2 y2 x3 y3 color]} @shape]
         (dispatch-shape (shapes/triangle [x1 y1] [x2 y2] [x3 y3] color)))
      "Add"]]))

(defn text-input []
  (let [shape (rf/subscribe [::subs/current-shape])]
    [:div {:style {:display "flex"}}
     [span {:margin-right 5} "x"]
     [num-input :x]

     [span {:margin-left 5, :margin-right 5} "y"]
     [num-input :y]

     [span {:margin-left 5, :margin-right 5} "size"]
     [num-input :size]

     [span {:margin-left 5, :margin-right 5} "text"]
     [:input {:type "text"
              :placeholder "Hello, world!"
              :on-change #(rf/dispatch [::events/assoc-shape :text (get-value %)])}]

     [span {:margin-left 5, :margin-right 5} "color"]
     [color-picker]

     [button {:style {:margin-left "auto"}}
      #(let [{:keys [x y size color text]} @shape]
         (dispatch-shape (shapes/text [x y] size color text)))
      "Add"]]))
