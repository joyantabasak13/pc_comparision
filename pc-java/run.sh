#!/bin/sh

#start=$(date +%s)
#cargo run -- -b 1000 -p 1000 -c 1000 -e 1000
#end=$(date +%s)
#echo "Elapsed Time: $(($end-$start)) seconds"

javac pc_java.java
ts=$(date +%s%N)  
java pc_java
echo "Elapsed time $((($(date +%s%N) - $ts)/1000000)) Millisecond"
