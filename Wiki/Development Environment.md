[Home](Home)>[Moneydance](Moneydance Information)>[Development](Development)>Development Environment

# Development Environment

## Required Software

You will need a Java Development Kit.  Moneydance 2015 comes with the Java 1.8 runtime and Moneydance 2019 comes with Java 11. 

You will need something to run your build file such as ANT.

Any IDE will work, though one built for Java will help, for example Eclipse.

## Compiling Your Extension

Extensions are held in a Java zip file with an file extension of .mxt.  Basically this is a .jar file with the addition of a signature file.  This file is used by Moneydance to verify that an extension has been validated by InfiniteKind.  The .mxt file is created along with the signature file by running the **Keyadmin** application.  The provided build.xml has the code to do this:


```
#!Xml
	    <java newenvironment="true" 
	      classpathref="classpath"
	      classname="com.moneydance.admin.KeyAdmin">
	      <arg value="signextjar"/>
	      <arg value="${privkeyfile}"/>
	      <arg value="${privkeyid}"/>
	      <arg value="loadsectrans"/>
	      <arg line="${dist}/loadsectrans.mxt"/>
	    </java>
	

```
Before you can compile your code you need to create the keys to be used by **Keyadmin**.  You do this by running the build file with the target **genkeys**, i.e


```
#!cmd

ant genkeys
```
It will ask for a pass phrase and then generate the keys.  

Everytime you compile your extension you will need to enter the pass phrase.  This has the disadvantage that you can not use Eclipse to compile the code.  You will need to run this from a command line interface.

1. Set up a short cut from your desktop that starts a Command Line on the src directory for your extensions
2. run

```
#!cmd

ant extensionname
```
This will do a normal build of the java code and then run the **Keyadmin** app to add the signature file.  It will ask you for the pass phrase.

## Directory Structure ##

The directory structure for extensions is:


```
#!cmd

Moneydance
-- src
----priv_key   (generated)
----pub_key   (generated)
----com
------moneydance
--------modules
----------features
------------extensionname
--------------java files (*.java)
----build
------moneydance
--------modules
----------features
------------extensionname
---------------classfiles (*.class)
--lib
----extadmin.jar
----moneydance-dev.jar
--dist
----extensionname.mxt
--bin
----com
------moneydance
--------modules
----------features
------------extensionname
--------------class files (*.class)
```
The package name of extensions is com.moneydance.modules.features.extensionname

## Build File

The supplied build file uses the above structure and places the extension .mxt file in the **dist** directory. 

You will need to change the file to include a target for your extension.  It is supplied with the name 'myextension'.  Change all instances of 'myextension' to the name of your extension.  This name must match the directory name for the source which in turn must match the package name in your source files.

 If you want the build file to move the created .mxt file directly to the Moneydance fmodules directory you can amend the build file.

Add the following to the top of the build file after the "optimize" line:


```
#!xml

  <property name="install" value="C:\Users\Mike\.moneydance\fmodules"/>

```
Where the value points at your installation of Moneydance.

At the bottom of <target> </target> pair for your extension place the following just before the </target>:

```
#!xml

	    <delete file="${dist}/extensionname.mxt" verbose="true" failonerror="false" />
	    <move file="./s-extensionname.mxt" tofile="${dist}/extensionname.mxt" 	verbose="true" failonerror="false" />
	    <copy file="${dist}/extensionname.mxt" tofile="${install}/extension.mxt"

```
Remember to change 'extensionname' to the name of your extension.

## Use with Eclipse

Using Eclipse you have a number of features available to you.

1. Eclipse will compile as you go flagging errors and warnings.  This can aid you creating clean code
2. You can debug your extension at the source code level (see [Debugging](Debugging))
3. You can interface it with GIT and bitbucket to provide version control

[Back to Development](Development)