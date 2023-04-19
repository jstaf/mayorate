#!/usr/bin/env bash

set -eu
cd ..

if [ ! -d GraphicsLib ]; then
	# graphicslib
    wget https://bitbucket.org/DarkRevenant/graphicslib/downloads/GraphicsLib_1.6.1.7z
    7z x GraphicsLib*.7z
fi

if [ ! -d LazyLib ]; then
	# lazylib
    wget https://github.com/LazyWizard/lazylib/releases/download/2.7b/LazyLib.2.7b.zip
  	unzip LazyLib*.zip
fi

if [ ! -d Nexerelin ]; then
    wget https://github.com/Histidine91/Nexerelin/releases/download/v0.10.6d/Nexerelin_0.10.6d.zip
    unzip Nexerelin*.zip
fi

if [ ! -d MagicLib ]; then
    wget https://github.com/MagicLibStarsector/MagicLib/releases/download/0.46.1-rc03/MagicLib-0.46.1-rc03.zip
    unzip MagicLib*.zip
fi
