(ns adzerk.boot-jar2bin
  {:boot/export-tasks true}
  (:require [boot.core                    :refer :all]
            [boot.util                    :as    util]
            [clojure.java.io              :as    io]
            [clojure.string               :as    str]
            [adzerk.boot-jar2bin.launch4j :refer (write-launch4j-config)])
  (:import [java.io FileOutputStream FileWriter]))

(defmacro assert-required [& locals]
  `(do ~@(for [l locals] `(assert ~l (str "Required argument missing")))))

(defn- default-header
  [jvm-opts]
  (format "#!/bin/sh\n\nexec java %s -jar $0 \"$@\"\n\n\n"
          (str/join \space jvm-opts)))

(defn- jars-in-fileset
  [fileset]
  (->> (output-files fileset)
       (by-ext [".jar"])
       ; aether.uber.jar is added to the fileset somewhere in the
       ; aot-pom-uber-jar pipeline
       (not-by-name ["aether.uber.jar"])
       (map tmp-file)))

(deftask bin
  "Builds a standalone binary executable from an uberjar.

   If `file` is not specified, builds an executable for every jar file in the
   fileset."
  [f file       PATH    file   "The path to the uberjar."
   o output-dir PATH    str    "The output directory path."
   H header     FILE    file   "A file containing a custom header to place at the beginning of the executable."
   j jvm-opt    OPTIONS #{str} "The JVM options to pass to the Java launcher."]
  (with-pre-wrap fileset
    (let [jars   (if file [file] (jars-in-fileset fileset))
          tgt    (tmp-dir!)
          header (if header
                   (slurp header)
                   (default-header (or jvm-opt #{})))]
      (when-not (seq jars)
        (throw (Exception. "No jar files found.")))
      (doseq [jar jars]
        (let [bin-fname  (->> (.getName jar)
                              (re-matches #"(.+)\.jar")
                              second)
              tgt-file   (io/file tgt bin-fname)
              out-file   (when output-dir
                           (doto (io/file output-dir bin-fname)
                             io/make-parents))]
          (util/info "Creating %s binary...\n" bin-fname)
          (with-open [bin (FileOutputStream. tgt-file)]
            (io/copy header bin)
            (io/copy jar bin))
          (when out-file
            (util/info "Writing %s...\n" (.getPath out-file))
            (io/copy tgt-file out-file)
            (.setExecutable out-file true false))))
      (-> fileset (add-resource tgt) commit!))))

(deftask exe
  "If Launch4j is installed, uses it to build a standalone .exe file from an
   uberjar.

   If `file` is not specified, builds an executable for every jar file in the
   fileset."
  [f file       PATH    file   "The path to the uberjar."
   o output-dir PATH    str    "The output directory path."
   n name       NAME    sym    "The name of the project."
   m main       STR     sym    "The main class."
   d desc       STR     str    "A description of the project."
   c copyright  STR     str    "The project's copyright information."
   v version    VERSION str    "The project version number."
   j jvm-opt    OPTIONS #{str} "The JVM options to pass to the Java launcher."
   p jre-path   STR     str    "(optional) The path where Java is expected to be found on the end user's system."]
  (assert-required output-dir version name main desc copyright)
  (with-pre-wrap fileset
    (let [jars      (if file [file] (jars-in-fileset fileset))
          tgt       (tmp-dir!)]
      (when-not (seq jars)
        (throw (Exception. "No jar files found.")))
      (doseq [jar jars]
        (let [fname      (->> (.getName jar)
                              (re-matches #"(.+)\.jar")
                              second)
              xml-fname  (str fname ".xml")
              exe-fname  (str fname ".exe")
              xml-file   (io/file tgt xml-fname)
              out-file   (doto (io/file output-dir exe-fname) io/make-parents)]
          (with-open [xml (io/writer xml-file :encoding "UTF-8")]
            (write-launch4j-config {:jar-file     jar
                                    :out-file     out-file
                                    :main-class   main
                                    :project-name name
                                    :description  desc
                                    :version      version
                                    :copyright    copyright
                                    :jvm-opts     (or jvm-opt #{})
                                    :jre-path     jre-path}
                                   xml))
          (util/dosh "launch4j" (.getPath xml-file))))
      (-> fileset (add-resource tgt) commit!))))

