#!/bin/sh

### install gradle-profiler
# on Linux
#sdk install gradleprofiler
# on Mac
# brew install gradle-profiler

### set JAVA_HOME target JAVA 17
export JAVA_HOME="/Users/ray/Library/Java/JavaVirtualMachines/jbr-17.0.7/Contents/Home"
### clean
./gradlew clean
### Run
gradle-profiler --profile buildscan --project-dir ./ composeApp:assembleDebug