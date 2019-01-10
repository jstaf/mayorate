[![Build Status](https://travis-ci.org/jstaf/mayorate.svg?branch=master)](https://travis-ci.org/jstaf/mayorate)

mayorate
===============================

This repository adds a new faction with a completely new set of sprites, music,
sfx, and shaders. To install, simply extract and copy to your /mods subdirectory
(use the download from the forum thread unless you want to compile things
yourself). Requires ShaderLib and LazyLib also to be present. It's generally
safest to use the most up-to-date versions of those libraries if possible.

This mod adds a lot of cutting-edge technical features not present in the base
game. This includes, but is not limited to fancy stuff like dynamic lighting,
normal/materials mapping, music, true campaign-level scripting, and AOE weapons.

Submit bug reports either here in Github issues or to the forum thread (or PM
me) and I'll do my best to iron them out. Feel free to submit a pull request if
you have something clever to add.

(And yes, I did all of the art/coding/music.)

## Compiling the mod yourself

Instructions here are provided for Linux and Windows. If you are a Mac user,
follow the Linux instructions but note that I do not test on this platform.

*All instructions assume you've got `git` and a working JDK installed.*

### Linux

```bash
# cd to starsector's "mods/" subdirectory before you start
git clone https://github.com/jstaf/mayorate.git
./gradlew run
```

### Windows

You need to manually install GraphicsLib, LazyLib, and Nexerelin before you
start. Note that the mod does not depend on Nexerelin or GraphicsLib at runtime
(if the user doesn't want to use them), but has them as a compile-time
dependency to add in this extra functionality.

Useful links
+ [GraphicsLib](http://fractalsoftworks.com/forum/index.php?topic=10982.0)
+ [LazyLib](http://fractalsoftworks.com/forum/index.php?topic=5444.0)

```bash
# in starsector's "mods/" directory
git clone https://github.com/jstaf/mayorate.git
gradlew.bat run
```

Note that Linux is the only official platform that I will support for building
the mod (no Windows computer anymore). If you encounter issues with the build
script, please submit a PR.
