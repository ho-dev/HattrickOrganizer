.. _faq:


FAQ for developers
===============================


**Gradle: could not get unknown property ``POEDITOR_APIKEY`` for object of type ``de.jansauer.poeditor.POEditorExtension``**


In case you don't have a POEditor key, you simply need to remove the related reference: in ´build.gradle´ comment the line 
referring to ``POEDITOR_APIKEY``

.. code-block:: java
    
    // apiKey = POEDITOR_APIKEY´


**MacOS: ``Aqua`` exception: at runtime a series of runtime exceptions are thrown, and the UI doesn't work as expected...**
-------------------------------------------------------------------------------------------------------------------------

Make sure that the following VM Arguments are set ``-Dapple.laf.useScreenMenuBar=true -Dapple.awt.showGroupBox=true``
