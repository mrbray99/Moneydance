[Home](Home)>[Moneydance](Moneydance Information)>[Development](Development)>Debugging

# Debugging your extension

## Source Level Debugging

Moneydance is a Java application.  To debug your extension at the source code level you must start Moneydance from within your IDE.

Running Moneydance requires that your IDE has access to all of the Moneydance .jar's and is started in moneydance.jar. 

For Eclipse you add all of the Moneydance .jar's to your build path as external jars:

![buildpath.jpg](https://bitbucket.org/repo/4oKeEz/images/3016752197-buildpath.jpg)

Then create a debug configuration:

![debug.jpg](https://bitbucket.org/repo/4oKeEz/images/3045921810-debug.jpg)

Running your extension in debug mode will start Moneydance and will stop at the first breakpoint in your extension.

**Note:** When Moneydance  runs outside the IDE it will continue if an unhandled exception occurs.  If you run it from within the IDE it will stop on the first unhandled exception.  See below:

## error.log

Moneydance provides an error log.  It is in the main user directory and called errlog.txt.  If an exception occurs this will be displayed here.

You can not use *System.out* from within Moneydance but you can use System.err, this is directed to errlog.txt.

[Back to Development](Development)