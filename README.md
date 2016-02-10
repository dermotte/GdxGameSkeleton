# GdxGameSkeleton
A simple game skeleton for libGDx to start with. Supports different screens including menu and loading screens, loading via AssetManager and scalable viewport for Menu and loading. GameScreen has to be implemented. All other screens are optimized for 1920x1080, but are also scaled by a camera to the actual viewport. However, it's advisable to go for a 16:9 screen ratio.

## Features

* Menu works, just the items and the background need to be replaced
* Loading is done centrally, loading screen is implemented
* SoundManager manages effect sounds as well as adaptive music
* Credit screen is there, just change the text that scrolls around.

# How
It's a straight libGDX project based on Gradle. Use Android Studio to edit it and you are fine. Other than that use gradle[.bat|.sh] to build for Desktop, iOS, Android or Web. 

# Why?
Because at the last game jam it was so annoying not to have such a skeleton.
