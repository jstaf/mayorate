#!/bin/bash

# just so nothing weird happens
set -eu
set -o pipefail

MOD=$(basename $(pwd))
VERSION=$(grep version mod_info.json | grep -oP '\d+(\.\d+)*')
RELEASE=$MOD-$VERSION

# copy over mod to release folder
mkdir ../$RELEASE
cp -R . ../$RELEASE

# now delete everything we don't want
cd ../$RELEASE
rm -rf .git .idea out
rm -f .gitignore *.iml *.sh

cd ..
zip -r $RELEASE.zip $RELEASE
if [ -d $RELEASE ]; then
	rm -rf $RELEASE
fi

