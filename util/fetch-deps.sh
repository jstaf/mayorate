#!/usr/bin/env bash

set -eu
cd ..

if [ ! -d GraphicsLib ]; then
	# graphicslib
    wget https://bitbucket.org/DarkRevenant/graphicslib/downloads/GraphicsLib_1.5.1.7z
    7z x GraphicsLib*.7z
fi

if [ ! -d LazyLib ]; then
	# lazylib
    wget https://github.com/LazyWizard/lazylib/releases/download/2.7/LazyLib.2.7.zip
  	unzip LazyLib*.zip
fi

if [ ! -d Nexerelin ]; then
    wget https://github.com/Histidine91/Nexerelin/releases/download/v0.10.3f/Nexerelin_0.10.3f.zip
    unzip Nexerelin*.zip
fi
