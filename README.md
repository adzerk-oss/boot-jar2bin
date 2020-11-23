# boot-jar2bin

[![Clojars Project](http://clojars.org/adzerk/boot-jar2bin/latest-version.svg)](http://clojars.org/adzerk/boot-jar2bin)

A [Boot](https://boot-clj.github.io) plugin for producing standalone console executables from uberjars, inspired by [lein-bin](http://github.com/Raynes/lein-bin).

# Usage

Add `adzerk/boot-jar2bin` to your `build.boot` dependencies and require/refer in the task:

```clojure
(set-env! :dependencies '[[adzerk/boot-jar2bin "X.Y.Z" :scope "test"]])
(require '[adzerk.boot-jar2bin :refer :all])
```

`boot-jar2bin`'s `bin` and `exe` tasks can either take a jar file that you point it to, or be composed with the `jar` task in your Boot pipeline:

```
boot aot pom uber jar bin
boot bin --file target/existing-jar-0.1.0.jar
```

This will create a binary file, either for the file you specify, or if you don't specify a file, there will be one file for every jar in the fileset. Because files in the target directory cannot be made executable by Boot tasks, you'll have to do so manually:

```
chmod +x target/existing-jar-0.1.0
```

Alternatively, you can specify an output directory, and the files will be copied there *and* marked executable for you:

```
boot aot pom uber jar bin --output-dir bin
```

## JVM Options

JVM options can be supplied to both the `bin` and `exe` tasks:

```
boot aot pom uber jar bin --jvm-opt -Dfoo=bar --jvm-opt -Dbaz=quux
```

Alternatively, you can use `task-options!` in your `build.boot`:

```
(def jvm-opts #{"-Dfoo=bar" "-Dbaz=quux"})

(task-options!
  bin {:jvm-opt jvm-opts}
  exe {:jvm-opt jvm-opts})
```

These options will be passed to the Java launcher each time the executable is run.

## File Headers

The `bin` task creates binary files out of jars by simply prepending some lines, turning the file into an executable shell script that runs the jar file using `java -jar`. By default, the header it prepends is this (`<JVM-OPTS>` is where any JVM options you supply will go):

```
#!/bin/sh

exec java <JVM-OPTS> -jar $0 "$@"



```

If you'd like to use your own custom header (e.g. to include Java flags, set environment variables, etc.), you can put it in a file (say, `head.sh`), and use the `--header` option:

```
boot aot pom uber jar bin --header head.sh
```

> NOTE: When using a custom header, you must hard-code in any JVM options; the `--jvm-opt` flag is provided as a convenience for when you're using the default header only.

## Windows executables

The `exe` task can be used to create Windows executables from jar files. This requires that [Launch4j](http://launch4j.sourceforge.net) be installed on your system. Launch4j requires additional configuration, which is typically supplied via a specialized XML file, but the `exe` task allows you to supply the values via task options. The easiest way to do this is to include them in your `build.boot`:

```clojure
(task-options!
  exe {:name      'sandwich
       :main      'sandwich.core
       :version   "0.1.0"
       :desc      "Run this exe file if you like sandwiches."
       :copyright "2015 Earl of Sandwich"
       :jvm-opt   #{"-Dfoo=bar" "-Dbaz=baf"}})
```

Then, to create the executable, run the `exe` task, including an `--output-dir` argument to specify where you would like Launch4j to place the .exe file it outputs:

```
boot aot pom uber jar exe --output-dir bin
```

As with the `bin` task, you can also specify an existing jar file:

```
boot exe --file target/existing-jar-0.1.0.jar --output-dir bin
```

This will create an executable `bin/existing-jar-0.1.0.exe`.

## License

Copyright © 2015 Adzerk

Distributed under the Eclipse Public License version 1.0.
