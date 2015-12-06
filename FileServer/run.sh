#!/bin/bash

if [ $# -ne 2 ] 
then
echo -n "Invalid Number of arguments."
echo "Usage: ./run.sh <IP of RMI Registry> <Port of rmiregistry>"
exit 1;
fi
echo

if [ "$2" -le 0 ] || [ "$2" -ge 65537 ]
then
echo "Invalid Port Number"
exit 1
fi

echo
echo "***Compiling***"
javac *.java
echo
echo "***Compilation Completed***"

echo
echo "Generating stub"
rmic FileServer
echo
echo "***Starting Server*** with IP:'$1'  and Port:'$2' "
echo
java FileServer "$1" "$2"
rm *.class
