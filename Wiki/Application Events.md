[Home](Home)>[Moneydance](Moneydance Information)>[Development](Development)>[Specifics](Specifics)>Application Events

## URLs

As stated your Moneydance has asynchronous tasks running.  To communicate with other threads it uses a URL approach.  In your extension you register the URL for your extension with the:


```
#!java

context.registerFeature(this, "showconsole",getIcon("mrb icon2.png"),getName());

```
call in your init() method.  This tells Moneydance that this extension should be called with the **showconsole** url.

When you select your extension from the main menu Moneydance sends a **showconsole** url to your  invoke(String url) method.  The standard response is to create a new window using the createAndShowGUI() method.  Note this is called within a Runnable object that is added to the Swing Event Queue using SwingUtilites.invokeLater.

If you wish to send a URL to another extension or to another part of Moneydance you can construct a URL and call context.showURL(url).

The formats of the URL are
```
#!java

moneydance:{command}?label={message}
```
This is sent to the main Moneydance processor.  {command} can be any of these:

|Command||purpose|
|-----------------|-------------------------------------------------|
|gohome|Go to the home page|
|showgraphs|Show the available graphs|
|showreports|Show the available reports|
|showgraph|Shows a particular graph|
|showreport|Shows a particular report|
|showcoa|Shows a list of accounts (equivalent Tools/Accounts)|
|showcategories|Shows a list of categories (equivalent Tools/Categories)|
|showbudgets|Shows a list of budgets (equivalent Tools/Budgets)|
|netsync|Calls the sync mechanism (equivalent to File/Syncing|
|showsearch||
|exit|Closes down Moneydance|
|showabout|Shows the about dialog (equivalent Help/About)|
|showprefs|Shows the preferences dialog (equivalent File/Preferences)|
|setstatus||
|setprogress|Displays a message at the bottom of the screen|
|importfile|Shows the Import File dialog (Equivalent to File/Import)|
|importprompt||
|showtxn|Displays the transaction detail|
|editreminders|Displays the Reminder dialog (equivalent to Tools/Reminders)|
|remindershome||
|budgethome||
|showupcomingreminders||

If the format of the URL is:

```
#!java

moneydance:fmodule:{extension}:{command}
```

The url is sent to the Extension invoke method with the url as a parameter.  It is up to the extension to deal with it as it sees fit.
 
 ## Events

Moneydance will send a number of event notification to an extension.  To handle these the extension should override the handleEvent method of FeatureContext, for example:


```
#!Java

@Override
public void handleEvent(String appEvent) {

    if ("md:file:opened".equals(appEvent)) {
        // TODO open processing
    }

    if ("md:file:closing".equals(appEvent)) {
        // TODO close processing
    }

}
```
The events that can be sent are:

|  Event |Purpose|
|------------|-----------------------------|
|md:file:closing|The Moneydance file is being closed|
|md:file:closed|The Moneydance file has closed|
|md:file:opening|The Moneydance file is being opened|
|md:file:opened|The Moneydance file has opened|
|md:file:presave|The Moneydance file is about to be saved|
|md:file:postsave|The Moneydance file has been saved|
|md:app:exiting|Moneydance is shutting down|
|md:account:select|An account has been selected by the user|
|md:account:root|The root account has been selected|
|md:graphreport|An embedded graph or report has been selected|
|md:viewbudget|One of the budgets has been selected|
|md:viewreminders|One of the reminders has been selected|
|md:licenseupdated|The user has updated the license