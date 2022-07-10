.. installing\_ho:

Installing HO!
==============

When you land on the release page, you see a lof of different packages.
There are packages for the various OS, for different installation type
and with or without bundled JRE. In case you are not sure which one you
should download please read the first section of this document.

Select the right package
**************************

Installers or portable application
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

You can use HO! on Windows, Linux and macOS.

You can decide to install it on your machine (menu integration) or to
use it as a `portable
application <https://en.wikipedia.org/wiki/Portable_application>`__. You
can have a single version of HO! installed on your machine but you might
have many portable versions. Hence, before upgrading your main HO! you
could decide to download a new version as a portable application to test
it first.

Manage Java dependency or use a bundled version
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Hattrick Organizer! requires Java. HO! 3.0 requires a JRE >= 8.0 and HO!
4.0 a JRE ≥= 14.0. Starting with HO! 4.0, you are not force to install
java on your computer and you can instead download a version of HO!
which bundles the appropriate JRE. The choice is yours.

The HO! packages bundled with a JRE can be identified by their name
explicitly mentioning it. Those packages are also much heavier as they
contain the JRE.

Latest stable version
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

All versions of HO! can be downloaded from the release page but usually
you will want only to use the latest `stable
version <https://github.com/akasolace/HO/releases/latest>`__

Directory structure
****************************************************

In the follow, the running application file are referring to: - /db -
user.json - error.log and output.log

:guilabel:`/db` folder contains whole you data. It is by far the most
important elements and the one you should always backup** before doing
anything fancy like testing a development version. In case you have
multiple team you will have multiple db folder.

:guilabel:`user.json` is the file linking your db folders to the user. In case
you have a single team and you kept HO! default it will look like:

.. code-block:: json
   :linenos:
   
   [   {     "teamName": "user",     "dbName": "db",     "backupLevel": 3,     "isNtTeam": false   } ]

If you have multiple teams, it could be like:

.. code-block:: json
   :linenos:
   
   [   {     "teamName": "FC Team",     "dbName": "db",     "backupLevel": 5,     "isNtTeam": false   },   {     "teamName": "Reserve Team",     "dbName": "db_reserve",     "backupLevel": 3,     "isNtTeam": false   },   {     "teamName": "National Team XXX",     "dbName": "dbNationalTeam",     "backupLevel": 3,     "isNtTeam": true   } ]

:guilabel:`error.log` and :guilabel:`output.log` are here for debug purposes

Portable application
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you are using a portable application all the files and folder will be
created within the application directory. You can move this directory
(e.g. on a USB stick or a network drive) and the app will continue to
work as expected.


Installed application
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Under Windows and MacOS, you might have warning about the fact that HO! is a non-signed app.
You should still feel perfectly safe to install it, the message only means we are not paying 
an (expensive) digital certificate to sign a free app. If you are interested on that topic, you can find more information 
`here <https://medium.com/vchaincodenotary/developers-unite-against-the-expensive-and-cumbersome-code-signing-certificates-d54342016a64>`__



Windows
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you installed HO! in Windows. You should have a HO! link in your
start menu that points toward the program executable. By default, HO!
installs itself in ``C:\Program Files\HO``. The running application
documents (``db/``, ``logs/`` and ``users.json``) will be located in ``%appdata%\HO``


Linux
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you installed HO! in Linux. You should have a HO! launcher that
points toward the program executable. By default, HO! installs itself in
``/opt/HO``. The running application files will be located in ``~/.ho``
except the ``error.log`` and ``output.log`` files which will created in
the installation directory

macOS
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you installed HO! in macOS. You usually have drag and drop HO! in
your applications folder. The running application files will be located
in ``/Library/Application Support/HO`` except the ``error.log`` and
``output.log`` files which will created in the installation directory



How to upgrade from HO! 3.0 to HO! 4.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. danger::
   Do not attempt upgrade before doing a **backup of your db** folder(s) !

It will not be possible to directly upgrade from HO! 3.0 to HO! 4.0, some (quick) manual steps will be required.

**If you have only one team and never played with database/user administration**, you only need to perform a fresh install of HO! 4.0 and to replace the content of the ``db`` folder with your existing database

**If you have more than one team or that you played with database/user administration**, you will have to manually create a file user.json that preserve the structure logic of your user.xml file. In that case you need to perform the following steps:

1) before upgrading, check your existing HO structure: :guilabel:`File>Database>Database/User administration`

.. figure:: https://user-images.githubusercontent.com/1136496/93209267-28b29480-f75e-11ea-8c5f-5f34378fdee0.png

2) close your HO then make a backup from your db(s), they are in your installation folder :ref:`directory-structure`

3) install HO! 4.0

4) Launch HO! and go to :guilabel:`File>Database>Database/User administration`. Use buttons :guilabel:`[Edit]` and :guilabel:`[Add]` to adjust the db structure to your needs.

5) replace the contents of ``db`` folder(s) with the backup(s) you made at step 2

Based on the previous example, here below is a side by side comparison of ``user.xml`` and ``user.json``

``user.xml``:

.. code-block:: xml
   
    <?xml version='1.0' encoding='UTF-8' ?> 
    <HoUsers>  
    <User>    
      <Name><![CDATA[FC Team]]></Name>    
      <Url><![CDATA[jdbc:hsqldb:file:db/database]]></Url>    
      <User><![CDATA[sa]]></User>    
      <Password><![CDATA[]]></Password>    
      <Driver><![CDATA[org.hsqldb.jdbcDriver]]></Driver>    
      <BackupLevel>3</BackupLevel>  
    </User>  
    <User>    
      <Name><![CDATA[Reserve Team]]></Name>    
      <Url><![CDATA[jdbc:hsqldb:file:db_reserve/database]]></Url>    
      <User><![CDATA[sa]]></User>    
      <Password><![CDATA[]]></Password>    
      <Driver><![CDATA[org.hsqldb.jdbcDriver]]></Driver>    
      <BackupLevel>3</BackupLevel>  
    </User>  
    <User>    
      <Name><![CDATA[National Team XXX]]></Name>    
      <Url><![CDATA[jdbc:hsqldb:file:dbNationalTeam/database]]></Url>    
      <User><![CDATA[sa]]></User>    
      <Password><![CDATA[]]></Password>   
      <Driver><![CDATA[org.hsqldb.jdbcDriver]]></Driver>    
      <BackupLevel>3</BackupLevel>  
    </User> 
  </HoUsers>



``user.json``:

.. code-block:: json
   
   [ 
   {     
      "teamName": "FC Team",     
      "dbName": "db",
      "backupLevel": 5,
      "isNtTeam": false
      },
      {
       "teamName": "Reserve Team",     
       "dbName": "db_reserve",     
       "backupLevel": 3,     
       "isNtTeam": false   
       },   
       {     
       "teamName": "National Team XXX",     
       "dbName": "dbNationalTeam",     
       "backupLevel": 3,     
       "isNtTeam": true   
       } 
      ]


First connection
**********************

For the first connection "F11" or, in the menu bar, "file" -> "download"

You see in the middle of the software a button with "open url" written
on it

.. figure:: https://user-images.githubusercontent.com/114285/82762498-49468f80-9df9-11ea-8bcc-23558972e0cf.png
   :alt: Screenshot 2020-05-24 at 19 48 32

   Screenshot 2020-05-24 at 19 48 32
Click on it and a link appears just below the button while opening a
hattrick page in your browser (if this page does not open, copy and
paste the link in the browser)

The link looks like this:
https://chpp.hattrick.org/oauth/authorize.aspx.…

.. figure:: https://user-images.githubusercontent.com/114285/82762531-75faa700-9df9-11ea-8f4b-54a067038187.png
   :alt: Screenshot 2020-05-24 at 19 48 46

   Screenshot 2020-05-24 at 19 48 46
Enter your login details as requested, Hattrick will give you a code
Copy the code into hattrick organizer

.. figure:: https://user-images.githubusercontent.com/114285/82762513-611e1380-9df9-11ea-89c1-a010174c0537.png
   :alt: Screenshot 2020-05-24 at 19 48 58

   Screenshot 2020-05-24 at 19 48 58
Little trick: select the code then CTRL + C to copy it and CTRL + V to
paste it

.. figure:: https://user-images.githubusercontent.com/114285/82762599-d1c53000-9df9-11ea-8bb2-440ed6063ffb.png
   :alt: Screenshot 2020-05-24 at 19 53 58

   Screenshot 2020-05-24 at 19 53 58
Please note that you should not enter your Hattrick password in any
third-party tools, make sure you do not enter your Hattrick password
anywhere in HO.

HRFs files
**********************

Storage of your hrf: it is not mandatory but if you decide to store
them, please make sure to select a folder in which you have writing
rights.

For another team: In the "Team Selection" window, select your other team
Then select the folder where you want to store his hrf (another folder
than that of your first team)

Modules
***********

All modules are not enabled by default, select and set the ones you plan
to use (File → Preferences → Modules tab)

-  automatic = activated in the tabs when HO starts!
-  enabled = available in functions but not loaded at startup
-  disabled = unavailable, unless you activate them ^^

for "team analyzer" and "Player analysis", there are other options that
can be activated.

Restoring database
**********************

If you encounter any problem, make a backup from your folder db (and
file user.xml if you have more than one team)

In your db folder are zip files. Extract one of them into your current
folder. You get a warning message if you want to overwrite existing
files, confirm it with “Yes”.

Add another team to HO
*********************************

It is possible to have multiple teams managed by HO! (c.f.
:ref:``second_team``)
