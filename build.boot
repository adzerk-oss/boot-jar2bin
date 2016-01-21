(set-env!
  :source-paths   #{"test"}
  :dependencies   '[[org.clojure/clojure  "1.7.0"  :scope "provided"]
                    [adzerk/bootlaces     "0.1.13" :scope "test"]
                    [org.clojure/data.xml "0.0.8"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "1.1.0")

(bootlaces! +version+)

(task-options!
  pom {:project     'adzerk/boot-jar2bin
       :version     +version+
       :description "Boot task to create standalone executable binaries."
       :url         "https://github.com/adzerk-oss/boot-jar2bin"
       :scm         {:url "https://github.com/adzerk-oss/boot-jar2bin"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

