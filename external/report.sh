#!/bin/sh

export WORKDIR=$(pwd)
export WORK_COMMTER="$(git show --pretty=format:%an --no-notes)"
export WORK_EMAIL="$(git show --pretty=format:%ae --no-notes)"
cd ~
git clone --depth=50 git@gist.github.com:/b6e8d189e7f4eb0edc51.git indication/gist || { echo Failed to checkout repository! ; cd $WORKDIR; exit 1; }
git config --global user.email "$WORK_EMAIL"
git config --global user.name "$WORK_COMMTER"
cd indication/gist
! test -d OpenRedmine && mkdir OpenRedmine
cp -R $WORKDIR/OpenRedmine/build/outputs OpenRedmine
! test -d Transdroid && mkdir Transdroid
cp -R $WORKDIR/Transdroid/build/outputs Transdroid
git add -A 
git commit -a -m "Update $BUILD_RESULT build from Travis-CI $TRAVIS_JOB_ID $TRAVIS_BRANCH $TRAVIS_TAG $TRAVIS_COMMIT_RANGE current $TRAVIS_COMMIT"
git push origin master
cd $WORKDIR
