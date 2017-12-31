(ns ddraw.aws)

(def error-handler (partial println "Error:"))
(def success-handler (partial println "Response:"))

(defn response-handler
  ([]
   (response-handler success-handler))
  ([success-fn]
   (response-handler success-fn error-handler))
  ([success-fn error-fn]
   (fn [err data]
     (if err
       (error-fn (.-code err) (.-message err))
       (success-fn data)))))
