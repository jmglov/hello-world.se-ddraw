(ns ddraw.shapes
  (:require [ddraw.colors :as colors]))

(defn rectangle [x y width height color]
  [:rect {:style {:stroke-width 0}
          :x x, :y y
          :width width, :height height
          :fill (colors/->html-color color)}])
