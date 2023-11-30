if [ `uname -o` = "Android" ]; then
	echo "Running on Android"
	java -Djava.library.path=/data/data/com.termux/files/usr/lib/jansi -jar ./build/libs/advent-of-code_2023-1.0-SNAPSHOT-all.jar $@
else
	java -jar ./build/libs/advent-of-code_2023-1.0-SNAPSHOT-all.jar $@
fi
