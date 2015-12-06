#!/bin/bash

function valid_ip
{
    local  ip=$1
    local  stat=1

    if [[ $ip =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$ ]]; then
        OIFS=$IFS
        IFS='.'
        ip=($ip)
        IFS=$OIFS
        [[ ${ip[0]} -le 255 && ${ip[1]} -le 255 \
            && ${ip[2]} -le 255 && ${ip[3]} -le 255 ]]
        stat=$?
    fi
    return $stat
}


echo "Validating Input Params"

if [ $# -ne 3 ] 
then
echo -n "Invalid Number of arguments. "
echo "Usage java ReadWriteClient  <Filename>  <IP of server>  <Port of rmiregistry>"
exit 1;
fi
echo
if [ ! -f "$1" ] 
then
echo "Invalid Input File."
exit 1;
fi
echo
valid_ip "$2"
echo
if [ $? -ne 0 ]
then
echo "Invalid Ip address"
exit 1 # exit with non zero status
fi

echo
if [ "$3" -le 0 ] || [ "$3" -ge 65537 ]
then
echo "Invalid Port Number"
exit 1
fi
echo
echo "Validation Completed"
echo 
echo "***Compiling***"
echo
javac *.java
echo
echo "***Executing***"
echo
echo "$1" "$2" "$3"
java ReadWriteClient "$1" "$2" "$3"
rm *.class
echo
echo "***Completed***"
