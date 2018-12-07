# Contributing to HO

Whether you are a novice or experienced software developer, all contributions and suggestions are welcome!
If you are looking to contribute to the HO codebase, the best place to start is the GitHub "issues" tab.
This is also a great place for filing bug reports and making suggestions for ways in which we can improve the code and documentation.

## Filing Issues

If you notice a bug in the code or in docs or have suggestions for how we can improve either, feel free to create an issue on the GitHub "issues" tab using GitHub's "issue" form. The form contains some questions that will help us best address your issue. For more information regarding how to file issues against pandas, please refer to the Bug reports and enhancement requests section of our main contribution doc.

## Getting Started

The code is hosted on GitHub, so you will need to use Git to clone the project and make changes to the codebase. 
It is also suggested to use IntelliJ IDEA as the main IDE. In case you decide to use Eclipse make sure to add all the relevant filters in the .ignore accordingly.


## Contributing to the Codebase

HO follow the following principle:

> **The contributor creates a small branch that represents a single feature, and when that branch is pushed to the contributor's fork they create a Pull Request.**

In case you are not familiar with project contribution on GitHub, we suggest you read first some [documentation](https://guides.github.com/activities/forking/)

### 1. Fork HO

You will need your own fork to work on the code. Go to the HO project page and hit the Fork button. 

### 2. Clone your fork to your machine:

~~~
git clone https://github.com/your-user-name/HO.git HO-yourname
cd pandas-yourname
git remote add upstream https://github.com/akasolace/ho.git 
~~~

This creates the directory HO-yourname and connects your repository to the upstream (main project) HO repository.


### 3. Create a branch ***for a single feature***

You want your master branch to reflect only production-ready code, so create a feature branch for making your changes. For example:

~~~
git branch shiny-new-feature
git checkout shiny-new-feature
~~~

The above can be simplified to:

~~~
git checkout -b shiny-new-feature
~~~

This changes your working directory to the shiny-new-feature branch. **Keep any changes in this branch specific to one bug or feature** so it is clear what the branch brings to HO. You can have many shiny-new-features and switch in between them using the git checkout command.

When creating this branch, make sure your master branch is up to date with the latest upstream master version. To update your local master branch, you can do:

~~
git checkout master
git pull upstream master --ff-only
~~

*When you want to update the feature branch with changes in master after you created the branch, check the section on [updating a PR](#update-your-pull-request)*

Before submitting your changes for review, make sure to pull latest vesrion of master and check that HO builds with your changes. 
Once your changes are ready to be submitted, make sure to push your changes to GitHub before creating a pull request. 
You will most likely be asked to make additional changes before it is finally ready to merge. 
However, once it's ready, we will merge it, and you will have successfully contributed to the codebase!


Contributing your changes to pandas
Committing your code


Once you’ve made changes, you can see them by typing:

```git status```

If you have created a new file, it is not being tracked by git. Add it by typing:

```git add path/to/file-to-be-added.py```

Doing ‘git status’ again should give something like:

~~~
# On branch shiny-new-feature
#
#       modified:   /relative/path/to/file-you-added.py
#
~~~

Finally, commit your changes to your local repository with an **explanatory message**

Now you can commit your changes in your local repository:

```git commit -m```

### Push your changes

When you want your changes to appear publicly on your GitHub page, push your forked feature branch’s commits:

```git push origin shiny-new-feature```

Here origin is the default name given to your remote repository on GitHub. You can see the remote repositories:

```git remote -v```

If you added the upstream repository as described above you will see something like:

~~~
origin  git@github.com:yourname/HO.git (fetch)
origin  git@github.com:yourname/HO.git (push)
upstream        git://github.com/akasolace/ho.git (fetch)
upstream        git://github.com/akasolace/ho.git (push)
~~~

Now your code is on GitHub, but it is not yet a part of the HO project. For that to happen, a pull request needs to be submitted on GitHub.

### Review your code

When you’re ready to ask for a code review, file a pull request. Before you do, once again make sure that you have followed all the guidelines outlined in this document. You should also double check your branch changes against the branch it was based on:

    -Navigate to your repository on GitHub: https://github.com/your-user-name/ho
    -Click on Branches
    -Click on the Compare button for your feature branch
    -Select the base and compare branches, if necessary. This will be master and shiny-new-feature, respectively.

### make the pull request

If everything looks good, you are ready to make a pull request. A pull request is how code from a local repository becomes available to the GitHub community and can be looked at and eventually merged into the master version. This pull request and its associated changes will eventually be committed to the master branch and available in the next release. To submit a pull request:

    1. Navigate to your repository on GitHub
    2. Click on the Pull Request button
    3. You can then click on Commits and Files Changed to make sure everything looks okay one last time
    4. Write a description of your changes in the Preview Discussion tab
    5. Click Send Pull Request.

This request then goes to the repository maintainers, and they will review the code.

### Update your pull request

Based on the review you get on your pull request, you will probably need to make some changes to the code. In that case, you can make them in your branch, add a new commit to that branch, push it to GitHub, and the pull request will be automatically updated. Pushing them to GitHub again is done by:

```git push origin shiny-new-feature```

This will automatically update your pull request with the latest code.

Another reason you might need to update your pull request is to solve conflicts with changes that have been merged into the master branch since you opened your pull request.

To do this, you need to “merge upstream master” in your branch:

~~~
git checkout shiny-new-feature
git fetch upstream
git merge upstream/master
~~~

If there are no conflicts (or they could be fixed automatically), a file with a default commit message will open, and you can simply save and quit this file.

If there are merge conflicts, you need to solve those conflicts. See for example at https://help.github.com/articles/resolving-a-merge-conflict-using-the-command-line/ for an explanation on how to do this. Once the conflicts are merged and the files where the conflicts were solved are added, you can run git commit to save those fixes.

If you have uncommitted changes at the moment you want to update the branch with master, you will need to stash them prior to updating (see the stash docs). This will effectively store your changes and they can be reapplied after updating.

After the feature branch has been update locally, you can now update your pull request by pushing to the branch on GitHub:

```git push origin shiny-new-feature```

### Delete your merged branch (optional)

Once your feature branch is accepted into upstream, you’ll probably want to get rid of the branch. First, merge upstream master into your branch so git knows it is safe to delete your branch:

~~~
git fetch upstream
git checkout master
git merge upstream/master
~~~

Then you can do:

```git branch -d shiny-new-feature```

Make sure you use a lower-case -d, or else git won’t warn you if your feature branch has not actually been merged.

The branch will still exist on GitHub, so to delete it there do:

```git push origin --delete shiny-new-feature```

