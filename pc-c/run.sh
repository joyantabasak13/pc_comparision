#!/bin/sh
gcc producer-consumer.c -pthread
ts=$(date +%s%N)  
./a.out
echo "Elapsed time $((($(date +%s%N) - $ts)/1000000)) Millisecond"
