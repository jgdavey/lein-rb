(ns leiningen.ruby.hooks
  (:require [leiningen.ruby.bundler]
            [robert.hooke :refer [add-hook]]))

(defn activate []
  (add-hook #'leiningen.deps/deps
            #'leiningen.ruby.bundler/dep-hook)
  (add-hook #'leiningen.core.classpath/get-classpath
            #'leiningen.ruby.bundler/classpath-hook)
  (add-hook #'leiningen.jar/write-jar
            #'leiningen.ruby.bundler/jar-hook))
