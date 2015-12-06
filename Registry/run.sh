#!/bin/bash

if [ $# -ne 2 ] 
then
echo -n "Invalid Number of arguments. "
echo "Usage: ./run.sh <IP of RMI Registry> <Port of RMI Registry>"
exit 1;
fi


if [ "$2" -le 0 ] || [ "$2" -ge 65537 ]
then
echo "Invalid Port Number"
exit 1
fi
echo

#rm *.class

echo "***Compiling Sources***"
echo
javac *.java
echo
echo "***Compilation Completed***"
echo
echo "***Starting Registry***"
java RegistryServer "$1" "$2"
rm *.class
