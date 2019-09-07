(ns adzerk.boot-jar2bin.launch4j
  (:require [clojure.data.xml :refer :all]
            [clojure.xml      :as    xml]
            [clojure.string   :as    str])
  (:import [java.nio.file Path Paths]))

(defn- to-four-places
  [version]
  (let [places (str/split version #"\.")]
    (try
      (doall (map #(Integer/parseInt %) places))
      (if (> (count places) 4) (throw (NumberFormatException.)))
      (catch NumberFormatException e
        (throw (Exception. "Version string must consist of up to four numbers, separated by periods."))))
    (str/join "." (concat places (repeat (- 4 (count places)) "0")))))

(defn- filename-without-path
  [file]
  (let [path (Paths/get (.getPath file) (into-array [""]))]
    (str (.getFileName path))))

(defn launch4j-config
  [{:keys [out-file jar-file main-class
           project-name description version copyright
           jvm-opts jre-path]}]
  (sexp-as-element
    [:launch4jConfig
     [:headerType "console"]
     [:outfile (.getAbsolutePath out-file)]
     [:jar (.getAbsolutePath jar-file)]
     [:classPath
      [:mainClass main-class]]
     (into [:jre
            (if jre-path
              [:path jre-path]
              [:path])
            [:minVersion "1.7.0"]
            [:maxHeapSize "2048"]
            [:jdkPreference "preferJdk"]]
           (for [jvm-opt jvm-opts]
             [:opt jvm-opt]))
     [:versionInfo
      [:fileVersion (to-four-places version)]
      [:txtFileVersion version]
      [:fileDescription description]
      [:copyright copyright]
      [:productVersion (to-four-places version)]
      [:txtProductVersion version]
      [:productName project-name]
      [:internalName project-name]
      [:originalFilename (filename-without-path out-file)]]]))

(defn write-launch4j-config
  [opts file-writer]
  (emit (launch4j-config opts) file-writer))

