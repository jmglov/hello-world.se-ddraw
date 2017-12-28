(ns ddraw.core
  (:require [ddraw.config :as config]
            [ddraw.events :as events]
            [ddraw.views :as views]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(enable-console-print!)

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (r/render [views/main-panel]
            (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
