#!/usr/bin/env sh

if [ "$(uname -o)" = "Android" ]; then
	echo "Running on Android"
	JAVA_OPTS="-Djava.library.path=/data/data/com.termux/files/usr/lib/jansi"
fi

java $JAVA_OPTS -jar ./build/libs/advent-of-code-2023-1.0-SNAPSHOT-all.jar "$@"
