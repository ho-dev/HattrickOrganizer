#!/bin/sh

LAUNCHDIR="$(dirname "$0")"
LAUNCHDIR="`eval echo $LAUNCHDIR`"
HOHOME="~/.ho"
HOHOME="`eval echo $HOHOME`"
OLDHOHOME="~/.hattrickorganizer"
OLDHOHOME="`eval echo $OLDHOHOME`"

start(){
    HODIR=/usr/lib/ho

    if [ ! -d "${HOHOME}" ]
    then
        echo "creating ${HOHOME}"
        mkdir -m 755 "${HOHOME}"
        echo "copying required ressource"

        # Copy db from old version
        if [ -d ${OLDHOHOME}/db ]
        then
            cp -r ${OLDHOHOME}/db ${HOHOME}/db
        fi
    fi

    # check if version in lib is greater than version in home
    LIBVERSION=`unzip -q -c $HODIR/HO.jar META-INF/MANIFEST.MF | grep Implementation-Version | cut -d' ' -f 2`
    if [ -f $HOHOME/HO.jar ]
    then
        HOMEVERSION=`unzip -q -c $HOHOME/HO.jar META-INF/MANIFEST.MF | grep Implementation-Version | cut -d' ' -f 2`
    else
        HOMEVERSION=""
    fi

    if [[ "$LIBVERSION" > "$HOMEVERSION" ]]
    then
        cp -R "${HODIR}/." "${HOHOME}"
    fi

    # Start HOLauncher
    echo "Starting HO from ${HOHOME}...\n"
    cd "${HOHOME}"
    eval "java -cp \"./*\" -Dinstall.mode=pkg HOLauncher"
}

startfromzip(){
    # Start HOLauncher
    echo "Starting HO from ${LAUNCHDIR}...\n"
    cd "${LAUNCHDIR}"
    eval "java -cp \"./*\" HOLauncher"
}

if [ "/usr/bin" = "${LAUNCHDIR}" ]
then
   echo "started from installed application"
   start
else
   echo "started from zip"
   startfromzip
fi

exit 0
