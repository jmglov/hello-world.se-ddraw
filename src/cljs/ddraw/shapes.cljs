(ns ddraw.shapes
  (:require [clojure.string :as string]
            [ddraw.colors :as colors]))

(defn circle
  ([pt radius color]
   (circle pt radius color 0))
  ([[x y] radius color outline-width]
   [:circle {:style {:stroke-width outline-width}
             :cx x, :cy y
             :r radius
             :fill (colors/->html-color color)}]))

(defn polygon
  ([points color]
   (polygon points color 0))
  ([points color outline-width]
   [:polygon {:style {:stroke-width outline-width}
              :points (->> points
                           (map #(string/join "," %))
                           (string/join " "))
              :fill (colors/->html-color color)}]))

(defn rectangle
  ([pt width height color]
   (rectangle pt width height color 0))
  ([[x y] width height color outline-width]
   [:rect {:style {:stroke-width 0}
           :x x, :y y
           :width width, :height height
           :fill (colors/->html-color color)}]))

(defn text [[x y] size color text]
  [:text {:x x, :y y
          :font-size size
          :fill (colors/->html-color color)
          :text-anchor :middle}
   text])

(defn triangle
  ([pt1 pt2 pt3 color]
   (triangle pt1 pt2 pt3 color 0))
  ([pt1 pt2 pt3 color outline-width]
   (polygon [pt1 pt2 pt3] color outline-width)))
