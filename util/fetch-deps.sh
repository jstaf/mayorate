#!/usr/bin/env bash

set -eu
cd ..

if [ ! -d GraphicsLib ]; then
	# graphicslib
    wget https://bitbucket.org/DarkRevenant/graphicslib/downloads/GraphicsLib_1.9.0.7z
    7z x GraphicsLib*.7z
fi

if [ ! -d LazyLib ]; then
	# lazylib
    wget https://github.com/LazyWizard/lazylib/releases/download/2.8b/LazyLib.2.8b.zip
  	unzip LazyLib*.zip
fi

if [ ! -d Nexerelin ]; then
    wget https://github.com/Histidine91/Nexerelin/releases/download/v0.11.1b/Nexerelin_0.11.1b.zip
    unzip Nexerelin*.zip
fi

if [ ! -d MagicLib ]; then
    wget https://github.com/MagicLibStarsector/MagicLib/releases/latest/download/MagicLib.zip
    unzip MagicLib*.zip
fi
