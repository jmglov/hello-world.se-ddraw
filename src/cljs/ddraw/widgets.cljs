(ns ddraw.widgets
  (:require [cljs.tools.reader :refer [read-string]]
            [ddraw.shapes :as shapes]
            [reagent.core :as r]))

(defn reset-to-element-value
  ([atom element]
   (reset-to-element-value atom element identity))
  ([atom element f]
   (println "Resetting" atom "to value:" (f (-> element .-target .-value)))
   (reset! atom (f (-> element .-target .-value)))))

(defn reset-seq! [v atoms]
  (doseq [a atoms]
    (reset! a v)))

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

(defn color-picker [color-atom]
  (reset! color-atom (first colors))
  [:select
   {:on-change #(reset-to-element-value color-atom %)}
   (map (fn [c] [:option {:key c} c]) colors)])

(defn num-input [num-atom]
  [:input {:type "text"
           :size 1
           :on-change #(reset-to-element-value num-atom % read-string)}])

(defn input [type atom]
  [:input {:type (keyword type)
           :on-change #(reset-to-element-value atom %)}])

(defn rectangle-input [on-add-fn]
  (let [x (r/atom 0)
        y (r/atom 0)
        width (r/atom 0)
        height (r/atom 0)
        color (r/atom (first colors))]
    [:div
     "x" (num-input x)
     "y" (num-input y)
     "width" (num-input width)
     "height" (num-input height)
     "color" (color-picker color)
     (button #(do
                (println [@x @y] @width @height @color)
                (let [shape (shapes/rectangle [@x @y] @width @height @color)]
                  (on-add-fn shape)
                  #_(reset-seq! nil [x y width height color])))
             "Add")]))
