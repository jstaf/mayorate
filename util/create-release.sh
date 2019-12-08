#!/usr/bin/env bash
set -eu

MOD=$(basename "$(pwd)")
VERSION=$(jq -r .version mod_info.json)
RELEASE=$MOD-$VERSION

# copy over mod to release folder
mkdir $MOD
cp -R data/ graphics/ sounds/ src/ $MOD/
cp changelog.txt CONTRIBUTORS.md README.md LICENSE mod_info.json ${MOD}.jar ${MOD}_settings.json ${MOD}.version $MOD/

zip -r $RELEASE.zip $MOD
rm -rf $MOD
