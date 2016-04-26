#!/bin/sh
dir=$(dirname "$0")
java -cp "$dir/jar/h2-1.4.190.jar:$H2DRIVERS:$CLASSPATH" org.h2.tools.Console "$@"
