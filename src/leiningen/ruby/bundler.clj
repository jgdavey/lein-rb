(ns leiningen.ruby.bundler
  (:require [clojure.java.io :as io :refer [file]]
            [leiningen.core.eval :refer [eval-in-project]]
            [leiningen.ruby.run :as run]
            [leiningen.ruby.utils :refer [gem-dir-for-project]]
            [leiningen.deps]
            [leiningen.jar]
            [leiningen.core.classpath]))

(def ^{:dynamic true} *fetch-deps* true)

(defn delete-tree [root]
  (doseq [child (.listFiles root)]
    (delete-tree child))
  (.delete root))

(defn dir-filespecs [dir parent-path]
  (apply concat
    (for [child (.listFiles dir)
          :let [path (str parent-path "/" (.getName child))]]
      (if (.isDirectory child)
        (dir-filespecs child path)
        [{:type :bytes
          :path path
          :bytes (with-open [content (io/input-stream child)]
                   (doto (byte-array (.length child))
                     (->> (.read content))))}]))))

(defn gem-filespecs-for-project [project]
  (let [gem-dir (gem-dir-for-project project)]
    (mapcat #(dir-filespecs (io/file gem-dir %) %)
            ["specifications" "bin" "gems"])))

(defn bundle-if-needed [project]
  (let [gem-dir (gem-dir-for-project project)
        gemfile (io/file (:root project) "Gemfile")
        gemfile-lock (io/file (:root project) "Gemfile.lock")
        cached-gemfile-lock (io/file (:root project) ".lein-gemfile-lock")]
    (when (and (.exists gemfile)
               (or (some #(not (.exists %))
                         [gem-dir gemfile-lock cached-gemfile-lock])
                   (or (not (.exists gemfile-lock))
                       (not (.exists cached-gemfile-lock))
                       (not= (slurp gemfile-lock)
                             (slurp cached-gemfile-lock)))))
      (doseq [subdir (.listFiles gem-dir)
              ; keep the cache directory around
              :when (not= (.getName subdir) "cache")]
        (delete-tree subdir))
      (binding [*fetch-deps* false]
        (run/run project ["gem" "install" "bundler"])
        (run/run project ["bundle" "-j" "4"]))
      (spit cached-gemfile-lock (slurp gemfile-lock)))))

(defn dep-hook [deps project & [cmd]]
  (let [fileset (deps project cmd)]
    (when *fetch-deps*
      (bundle-if-needed project))
    fileset))

(defn classpath-hook [get-classpath project]
  (cons (str (gem-dir-for-project project))
        (get-classpath project)))

(defn jar-hook [write-jar project jar-out filespecs]
  (write-jar project
             jar-out
             (concat filespecs
                     (gem-filespecs-for-project project))))
