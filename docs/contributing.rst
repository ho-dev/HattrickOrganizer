.. _contributing:

Contributing to HO
=========================

Whether you are a novice or experienced software developer, all contributions and suggestions are welcome! If you are looking to contribute to the HO codebase, the best place to start is the GitHub "issues" tab. This is also a great place for filing bug reports and making suggestions for ways in which we can improve the code and documentation.

Filing Issues
------------------------

If you notice a bug in the code or in docs or have suggestions for how we can improve either, feel free to create an issue or a feature request on the GitHub "issues" tab using prefilled form. Those form contains some questions that will help us best address your issue.

Getting Started
--------------------

The code is hosted on GitHub, so you will need to use Git to clone the project and make changes to the 
codebase. It is also suggested to use IntelliJ IDEA as the main IDE. In case you decide to use 
Eclipse make sure to add all the relevant filters in the .ignore accordingly.

In case of problems to set the development environment up, please consult our :ref:`faq` page to see 
the list of the most common issues.

Branches and releases
---------------------------------

HO always has two lines of development:

- ``master`` holds the next major version: new features, improvements and database changes. It builds
  the **dev** release.
- ``release/<N>`` (e.g. ``release/10``) holds major version N from its beta on and takes bug fixes only.
  It builds the **beta** and **stable** releases of N.x. The current release branch is the newest
  ``release/<N>``. It can be in its beta phase while the latest stable HO is still N-1.

Which branch your pull request goes to:

- **New feature, improvement, refactoring, new translation key or database schema change:** ``master``.
  It ships with the next major version.
- **Fix for a bug that is already in the newest release branch:** that ``release/<N>``, also when the bug
  was reported against an older version. Keep the change to the fix itself and reference the issue it
  fixes. A bug in code that only exists on ``master`` is fixed on ``master``. A fix that needs a database change or a new translation key goes to
  ``master``. Start the branch from the release branch, not from your master, for example
  ``git checkout -b fix-1234-short-description upstream/release/10``, and pick ``release/10`` as the
  base branch of the pull request.
- **Not sure:** ``master``. Say in the pull request that it may be worth backporting, and the
  maintainers decide.

You never open the same fix twice. The maintainers merge ``release/<N>`` into ``master`` regularly, so a
fix on the release branch reaches ``master`` by itself. Nothing is merged the other way.

When major version N enters its beta phase, ``release/<N>`` is branched off ``master``: it builds the
beta releases of N, and ``master`` moves on to N+1. Once N is declared stable, the same branch builds the
stable N.0, N.1, … releases. When the beta of N+1 starts, ``release/<N+1>`` takes over and
``release/<N>`` gets no further changes.

Contributing to the Codebase
---------------------------------

In case you are not familiar with project contribution on GitHub, we suggest you read first some 
appropriate `documentation <https://guides.github.com/activities/forking/>`_. 

Before submitting your changes for review, make sure to pull latest version of master and check that 
HO builds with your changes. Once your changes are ready to be submitted, make sure to push your 
changes to GitHub before creating a pull request. You will most likely be asked to make additional 
changes before it is finally ready to merge. However, once it's ready, we will merge it, and you will 
have successfully contributed to the codebase!

Translations
-----------------------------
You can simply do so via `PoEditor <https://poeditor.com/join/project/jCaWGL1JCl>`_

You need to fix both 'Fuzzy translation' (priority) and remaining untranslated terms. 
In case the language in which you would like to contribute is not listed in the project page 
please make a request by opening a ticket on GitHub
