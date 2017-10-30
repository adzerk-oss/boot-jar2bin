# CHANGELOG

## 1.1.1 (2017-10-30)

* Fixed an encoding issue when running the `exe` task on Windows. ([#2](https://github.com/adzerk-oss/boot-jar2bin/pull/2))

  Thanks to [Hemaolle] for the fix!

## 1.1.0 (2016-01-21)

* Both the `bin` and `exe` tasks now take a `--jvm-opt` flag for passing options to the Java launcher each time the executable is run.

## 1.0.0 (2015-11-15)

* `exe` task produces standalone Windows executables (.exe files) from jars, using Launch4j.

* Task names shortened (`jar2bin -> bin, jar2exe -> exe`) for the sake of simplicity.

## 0.1.0 (2015-11-08)

* Initial release. `jar2bin` task produces standalone Unix/Linux executables.

[Hemaolle]: https://github.com/Hemaolle
