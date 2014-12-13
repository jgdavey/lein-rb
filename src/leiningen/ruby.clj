(ns leiningen.ruby
  (:require [leiningen.ruby.run :as run]))

(defn ruby
  "Run a ruby command in the context of the project."
  [project & args]
  (run/run project args))
