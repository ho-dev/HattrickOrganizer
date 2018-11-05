#!/bin/sh

HOHOME="~/.ho"
HODIR=/usr/lib/ho/

HOHOME="`eval echo $HOHOME`"

if [ ! -d "${HOHOME}" ]
then
	echo "creating ${HOHOME}"
	mkdir -m 777 "${HOHOME}"
fi

if [ -e "/usr/lib/ho/update.zip" ]
then
rm /usr/lib/ho/update.zip
fi


if [ -e "$HOHOME/update.zip" ]
then
echo "update found"
cp -r "$HOHOME/update.zip" "${HODIR}"
rm "$HOHOME/update.zip"
fi

# Start HOLauncher
echo "Starting HO from ${HOHOME}...\n"
cd "${HOHOME}"
eval "java -cp \"${HODIR}*\" HOLauncher"
