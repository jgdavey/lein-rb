(ns leiningen.bundle
  (:require [leiningen.core.eval :as e]
            [leiningen.ruby.bundler :as bun]))

(defn bundle
  "Run a ruby command in the context of the project."
  [project & args]
  (bun/bundle-if-needed project))
