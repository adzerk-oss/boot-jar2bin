(set-env!
  :source-paths   #{"src"}
  :dependencies   '[[org.clojure/clojure  "1.8.0"  :scope "provided"]
                    [adzerk/bootlaces     "0.1.13" :scope "test"]
                    [org.clojure/data.xml "0.0.8"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "1.2.0")

(bootlaces! +version+)

(task-options!
  pom {:project     'adzerk/boot-jar2bin
       :version     +version+
       :description "Boot task to create standalone executable binaries."
       :url         "https://github.com/adzerk-oss/boot-jar2bin"
       :scm         {:url "https://github.com/adzerk-oss/boot-jar2bin"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask deploy
  "Installs release jar locally and deploys it to Clojars."
  []
  (comp (build-jar) (install) (push-release)))
