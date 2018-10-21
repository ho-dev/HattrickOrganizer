#!/bin/bash
if [ "x$2" == "x" ]; then
	echo "Usage: addString <STRINGNAME> <DEFAULTTEXT>"
	exit 1
fi
fitStringLength=25
for curLang in `cat languages`; do
	if [ -e ${curLang}.txt ]; then
		echo "Adding string to ${curLang}"
		stringLength=${#1}
		addSpaces=`expr ${fitStringLength} - ${stringLength}`
		echo -e -n '!'"insertmacro LANG_STRING" >> ${curLang}.txt
		echo -n " $1 " >> ${curLang}.txt
		while [ ${addSpaces} -gt 0 ]; do
			echo -n " " >> ${curLang}.txt
			addSpaces=`expr ${addSpaces} - 1`
		done
		echo -e "\"$2\"\r"  >> ${curLang}.txt
	else
		echo "${curLang}.txt not found, skipping"
	fi
done
