#!/bin/sh

LAUNCHDIR="$(dirname "$0")"
LAUNCHDIR="`eval echo $LAUNCHDIR`"
HOHOME="~/.ho"
HOHOME="`eval echo $HOHOME`"
OLDHOHOME="~/.hattrickorganizer"
OLDHOHOME="`eval echo $OLDHOHOME`"

start(){
    HODIR=/usr/lib/ho/

    if [ ! -d "${HOHOME}" ]
    then
        echo "creating ${HOHOME}"
        mkdir -m 755 "${HOHOME}"
        echo "copying required ressource"
        cp -R "${HODIR}/." "${HOHOME}"
        
        # Copy db from old version
        if [ -d ${OLDHOHOME}/db ]
        then
            cp -r ${OLDHOHOME}/db ${HOHOME}/db
        fi
    fi
    
    # Start HOLauncher
    echo "Starting HO from ${HOHOME}...\n"
    cd "${HOHOME}"
    eval "java -cp \"./*\" HOLauncher"
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
