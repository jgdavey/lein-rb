(ns leiningen.ruby.utils
  (:require [clojure.java.io :as io]))

(def default-settings
  {:ruby-gem-dir (str (io/file "vendor" "gems"))})

(defn gem-dir-for-project [project]
  (io/file (:root project)
        (:ruby-gem-dir (merge default-settings project))))
