#!/bin/sh

export WORKDIR=$(pwd)
export WORK_COMMTER="$(git log -1 --pretty=format:%an --no-notes)"
export WORK_EMAIL="$(git log -1 --pretty=format:%ae --no-notes)"
cd ~
git clone -b master --depth=10 git@github.com:indication/OpenRedmineBuilds.git indication/builds || { echo Failed to checkout repository! ; cd $WORKDIR; exit 1; }
git config --global user.email "$WORK_EMAIL"
git config --global user.name "$WORK_COMMTER"
cd indication/builds
! test -d OpenRedmine && mkdir OpenRedmine
test -d OpenRedmine/outputs && rm -rf OpenRedmine/outputs
cp -R $WORKDIR/OpenRedmine/build/outputs OpenRedmine
test -d OpenRedmine/reports && rm -rf OpenRedmine/reports
cp -R $WORKDIR/OpenRedmine/build/reports OpenRedmine
git add -A 
git commit -a -m "Update $BUILD_RESULT build from Travis-CI #$TRAVIS_JOB_NUMBER $TRAVIS_BRANCH $TRAVIS_TAG $TRAVIS_COMMIT_RANGE current $TRAVIS_COMMIT"
git push origin master
cd $WORKDIR
