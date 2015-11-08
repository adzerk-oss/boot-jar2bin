# boot-jar2bin

[![Clojars Project](http://clojars.org/adzerk/boot-jar2bin/latest-version.svg)](http://clojars.org/adzerk/boot-jar2bin)

A [Boot](http://boot-clj.com) plugin for producing standalone console executables from uberjars, inspired by [lein-bin](http://github.com/Raynes/lein-bin).

# Usage

Add `boot-jar2bin` to your `build.boot` dependencies and require/refer in the task:

```clojure
(set-env! :dependencies '[[adzerk/boot-jar2bin "X.Y.Z" :scope "test"]])
(require '[adzerk.boot-jar2bin :refer :all])
```

`boot-jar2bin` can either take a jar file that you point it to, or it can be composed with the `jar` task in your Boot pipeline:

```
boot aot pom uber jar jar2bin
boot jar2bin --file target/existing-jar-0.1.0.jar
```

This will create a binary file, either for the file you specify, or if you don't specify a file, there will be one file for every jar in the fileset. Because files in the target directory cannot be made executable by Boot tasks, you'll have to do so manually:

```
chmod +x target/existing-jar-0.1.0
```

Alternatively, you can specify an output directory, and the files will be copied there *and* marked executable for you:

```
boot aot pom uber jar jar2bin --output-dir bin
```

## File Headers

`boot-jar2bin` creates binary files out of jars by simply prepending some lines, turning the file into an executable shell script that runs the jar file using `java -jar`. By default, the header it prepends is this:

```
#!/bin/sh

exec java -jar $0 "$@"



```

If you'd like to use your own custom header (e.g. to include Java flags, set environment variables, etc.), you can put it in a file (say, `head.sh`), and use the `--header` option:

```
boot aot pom uber jar jar2bin --header head.sh
```

## Windows executables

TODO: [`jar2exe` task (using launch4j, probably)](https://github.com/adzerk-oss/boot-jar2bin/issues/1)

## License

Copyright © 2015 Adzerk

Distributed under the Eclipse Public License version 1.0.
