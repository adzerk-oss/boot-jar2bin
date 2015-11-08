(ns adzerk.boot-jar2bin
  {:boot/export-tasks true}
  (:require [boot.core :refer :all]
            [boot.util :as util]
            [clojure.java.io :as io])
  (:import java.io.FileOutputStream))

(def ^:private default-header
  "#!/bin/sh\n\nexec java -jar $0 \"$@\"\n\n\n")

(deftask jar2bin
  "Builds a standalone binary executable from an uberjar.

   If `file` is not specified, builds an executable for every jar file in the
   fileset."
  [f file       PATH file "The path to the uberjar."
   o output-dir PATH str  "The output directory path."
   H header     FILE file "A file containing a custom header to place at the beginning of the executable."]
  (comp
    (with-pre-wrap fileset
      (let [jars   (if file
                     [file]
                     (->> (output-files fileset)
                          (by-ext [".jar"])
                          ; aether.uber.jar is added to the fileset
                          ; somewhere in the aot-pom-uber-jar pipeline
                          (not-by-name ["aether.uber.jar"])
                          (map tmp-file)))
            tgt    (tmp-dir!)
            header (if header (slurp header) default-header)]
        (when-not (seq jars)
          (throw (Exception. "No jar files found.")))
        (doseq [jar jars]
          (let [bin-fname (->> (.getName jar)
                               (re-matches #"(.+)\.jar")
                               second)
                tgt-file  (io/file tgt bin-fname)
                out-file  (when output-dir
                            (doto (io/file output-dir bin-fname)
                              io/make-parents))]
            (util/info "Creating %s binary...\n" bin-fname)
            (with-open [bin (FileOutputStream. tgt-file)]
              (if header
                (io/copy header bin)
                (.write bin (.getBytes default-header)))
              (io/copy jar bin))
            (when out-file
              (util/info "Writing %s...\n" (.getPath out-file))
              (io/copy tgt-file out-file)
              (.setExecutable out-file true false))))
        (-> fileset (add-resource tgt) commit!)))))
