(ns leiningen.ruby.run
  (:require [clojure.java.io :as io]
            [leiningen.core.eval :refer [eval-in-project]]
            [leiningen.ruby.utils :refer [gem-dir-for-project]]))

(defn run-template [project all-args]
  (let [gem-dir (gem-dir-for-project project)
        [cmd & ruby-args] all-args
        gem-cmd (io/file gem-dir "bin" cmd)
        real-cmd (if (.exists gem-cmd)
                   (str gem-cmd)
                   cmd)
        ruby-args (if (= real-cmd "ruby")
                    ruby-args
                    (concat ["-S" real-cmd] ruby-args))
        gem-dir (str gem-dir)
        args (vec ruby-args)]
  `(-> (doto (org.jruby.RubyInstanceConfig.)
         (.setCompatVersion (org.jruby.CompatVersion/valueOf "RUBY1_9"))
         (.setEnvironment (merge (into {} (System/getenv))
                                 {"GEM_HOME" ~gem-dir
                                  "GEM_PATH" ~gem-dir
                                  "PATH" (str (java.io.File. ~gem-dir "bin")
                                              (System/getProperty "path.separator")
                                              (System/getenv "PATH"))})))
       (org.jruby.Main.)
       (.run (into-array String ~args))
       (.getStatus))))

(defn run [project args]
  (eval-in-project project (run-template project args)))
