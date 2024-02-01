[Home](Home)>[Moneydance](Moneydance Information)>[Development](Development)>Structure of an Extension

# The Structure of a Moneydance Extension

The main aspects of an extension are:

* The API
* The interface with Swing
* The Object Model
* The Preamble

## The API

Moneydance extensions should be written using the provided API.  There is a link to the API documentation on the development page but these pages are out of date.  The up to date pages are in the Developers Kit so download this and use the provided pages as your documentation.

 The development API is not a complete list of the classes used within Moneydance.  Though the full set of classes is available to you through the provided .jar's it is not wise to use them as they can change at any time.  Version 2015 has made major changes to the development API.  Extensions written for 2014 and earlier will not run on 2015, and vice versa.

The development API is in moneydance-dev.jar.

Unfortunately the documentation for the API is not complete.  There are definitions for all classes, interfaces, methods etc but not how they work.  The information presented here should help understanding how it works.

## Interface with Swing

Moneydance utilises AWT and Swing.  It has already set up an Event Dispatch Thread and therefore the GUI of your extension should not create new threads unless you manage the synchronization with Moneydance.  

## Object Model

Moneydance stores its data as serializations of the main data objects.  You gain access to the data via the API, not by reading a database.  Moneydance takes care of saving its data.

The Object Model is described in this Wiki ([see](Moneydance Object Model))

If you wish to save data as part of your extension this will have to be done outside Moneydance.  You can, however, get hold of the directory where your data is held and place files in that directory.  One approach is to create one or more objects that reflect your data and serialize them into the separate file.

## The Preamble

The start of any extension is the same. 

First the initial *Main* class must be a subclass of the *FeatureModule* class.

It must have two methods:

* **init()** this registers the extension and is called when the extension is loaded, i.e, when Moneydance starts, not when it is run
* **invoke(String uri)**  is the method called when the extension is selected by the user

It also has the concept of a *context*.  This is the main root into all data. It is obtained by calling the *getContext* method of *FeatureModule*.

### init

The extension must register itself via the *context.registerFeature* method.  This has the following parameters:

* the id of the Main class
* the Command to use on Invoke (see below)
* the icon to use (this is an old requirement and is not longer used but needs to be passed)
* the name of the extension

This method can throw an exception so the you need to catch it.

### invoke

This is the method that is called when the user selects the extension.  It is passed a URI containing the command to be executed.  This will be the same as the command registered for the extension (see init)

On receiving this command you can enter your code.

**Note:** Your extension can be entered more than once so you need to check to see if it has already been called.  

[Back to Development](Development)