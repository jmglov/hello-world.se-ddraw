(ns ddraw.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub ::authenticated? :authenticated?)
