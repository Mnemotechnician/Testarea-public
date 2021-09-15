# Testarea
Java modding attempt. Will implement my random bullshit ideas here.
The development goes in a private repo with tons of commits,
stable builds are uploaded here.

# Installation
## Via mod browser
1. Open mindus
2. Open the mods tab, then click "mod browser"
3. Type "Testarea" in the search bar
4. Press on the mod and click "install"
5. Restart the game

## From github
1. On [this page](https://github.com/MHeMoTexHuK/Testarea-public),
find the "releases" tab, press on the latest release
2. Download "testarea.jar" from assets
3. Import the mod: either open mindustry, go to the mods tab, press "import mod", select the downloaded
.jar file, or manually place the mod in the mods folder.
4. (Re)start the game


# Building manually
Assuming you have the required knowledge, patience and stubbornness. Idk how scared of getting a virus
you should be in order to use this approach...

#### (Applied to the paragraphes below)
0. Clone the repo and set it as the working directory or smth

## Building manually for desktop only
1. Install JDK **16**.
2. Run `gradlew jar` [1].
3. Your mod jar will be in the `build/libs` directory.
**Only use this version on desktop. It will not work with Android.**


## Building manually for Android & desktop
1. Download the Android SDK, unzip it and set the `ANDROID_HOME` environment variable to its location.
2. Make sure you have API level 30 installed, as well as any recent version of build tools (e.g. 30.0.1)
3. Add a build-tools folder to your PATH. For example, if you have `30.0.1` installed, that would be `$ANDROID_HOME/build-tools/30.0.1`.
4. Run `gradlew deploy`. If you did everything correctlly, this will create a jar file in the `build/libs` directory that can be run on both Android and desktop. 