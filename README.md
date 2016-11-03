# GdxScreen
A state machine for your game.

GdxState was the original project.  Many comments were made and considered to the result of a remake being made - GdxScreen.  It is provides an efficient way to handle all your game states / screens and the assets that they posses.  It also comes with additional functions to help with the overal game making process.  It is written using libGdx for libGdx.

GdxScreen helps to keep your game code more organised by putting all your transitional effects into special screens and then applying them to your game screens.  This allows your transitional effects to also be easily reuseable.

##Features
+ Separate transitional effects for loading and unloading screens into their own screens
+ Any screen can be loaded with any transition with one method call
+ Provides utility classes for doing some basic functions with POJOs such as saving and making preferences.
+ You can store any class instance into the game's library (GdxLibrary) and have them accessed by any of your game states / screens.

##Demonstration
A small demonstration is provided :https://github.com/Rickodesea/GdxScreen-Demo
