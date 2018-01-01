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

(defn button [on-click-fn label]
  [:button {:on-click on-click-fn} label])

(defn color-picker []
  [:select
   {:on-change #(rf/dispatch [::events/assoc-shape :color (get-value %)])}
   (map (fn [c] [:option {:key c} c]) colors)])

(defn num-input [k]
  [:input {:type "text"
           :placeholder "0"
           :size 1
           :on-change #(rf/dispatch [::events/assoc-shape k (read-string (get-value %))])}])

(defn login-form []
  (let [username (r/atom nil)
        password (r/atom nil)]
    [:div
     "Username:" [:input {:type "text", :on-change #(reset! username (get-value %))}]
     "Password:" [:input {:type "password", :on-change #(reset! password (get-value %))}]
     (button #(rf/dispatch [::events/login! @username @password]) "Login")]))

(defn rectangle-input []
  (let [shape (rf/subscribe [::subs/current-shape])]
    [:div
     "x" [num-input :x]
     "y" [num-input :y]
     "width" [num-input :width]
     "height" [num-input :height]
     "color" [color-picker]
     (button #(let [{:keys [x y width height color]} @shape]
                (let [shape (shapes/rectangle [x y] width height color)]
                  (rf/dispatch [::events/add-shape shape])
                  (rf/dispatch [::events/publish-command shape])
                  (rf/dispatch [::events/input-shape nil])))
             "Add")]))
