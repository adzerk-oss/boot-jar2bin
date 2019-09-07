# CHANGELOG

## 1.2.0 (2019-09-07)

* Added a `--jre-path` option to the `exe` task, allowing one to specify the
  path where Java is expected to be found on the end user's system.

  From my understanding, the launch4j default behavior is to locate Java via the
  Windows registry. However, there have been reported issues around some
  distributions of Java (e.g. AdoptOpenJDK 11) not adding the appropriate
  information to the registry, and apparently using whatever `java` is on the
  `PATH` is not the default behavior of the executable emitted by launch4j.

  According to discussions [here](https://sourceforge.net/p/launch4j/bugs/197/)
  and [here](https://sourceforge.net/p/launch4j/feature-requests/127/), you can
  configure launch4j to use `%JAVA_HOME%;%PATH%` in the
  `<jre><path>...</path></jre>` part of the config file, and that will make it
  so that the executable will try to find Java via the paths specified in the
  `%JAVA_HOME%` and `%PATH%` Windows environment variables.

  You can now do this by specifying `--jre-path "%JAVA_HOME%;%PATH%"` in the
  `exe` task options, and hopefully the executables you create will work if the
  end user has a distribution of Java that doesn't update the Windows registry
  appropriately.

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
