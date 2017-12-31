(ns ddraw.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub ::authenticated? :authenticated?)
(rf/reg-sub ::listening? :listening?)
(rf/reg-sub ::queue-created? :queue-created?)

(rf/reg-sub ::shapes :shapes)
(rf/reg-sub ::shape-input :shape-input)
