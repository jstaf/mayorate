#!/usr/bin/env bash

set -eu
cd ..

if [ ! -d GraphicsLib ]; then
	  # graphicslib
    wget https://bitbucket.org/DarkRevenant/graphicslib/downloads/GraphicsLib%201.4.1.7z
    7z x GraphicsLib*.7z
fi

if [ ! -d LazyLib ]; then
	  # lazylib
    wget https://bitbucket.org/LazyWizard/lazylib/downloads/LazyLib%202.4e.zip
	  unzip LazyLib*.zip
fi

if [ ! -d Nexerelin ]; then
    wget https://bitbucket.org/Histidine/exerelin/downloads/Nexerelin_0.9.5g.zip
    unzip Nexerelin*.zip
fi
