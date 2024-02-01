[Home](Home)>[Moneydance](Moneydance Information)>[Development](Development)>Specifics

# Specifics

Here are some pointers to help you with Extension Development.  They are not  in any specific order.  I will add to it in response to questions or particularly difficult things I discover.
## Contents

* [Behaviour](https://bitbucket.org/mikerb/moneydance-2019/wiki/Specifics#markdown-header-behaviour)
* [Closing and Events](https://bitbucket.org/mikerb/moneydance-2019/wiki/Specifics#markdown-header-closing-and-events)
* [Lists](https://bitbucket.org/mikerb/moneydance-2019/wiki/Specifics#markdown-header-lists)
* [SyncRecord](https://bitbucket.org/mikerb/moneydance-2019/wiki/Specifics#markdown-header-syncrecord)

##Behaviour

Moneydance is writen in Java and as such is an event driven application.  When actions are performed the process is carried out and then one or more events are triggered (fired) to inform other parts of the application that something has changed.  Extensions are no different.  The user can start an extension and then in the middle of using it go to someother part of MD, make some changes and return to the extension.  Those changes could effect your extension but you will not know it unless you listen for events.

The MD API has several interfaces and listeners spread across it.  A well behaved extension will listen for changes in the data and reload the changed data when an event is fired. 

I will document relevant listeners as I find them.

### Closing and Events

The way the model extension is written means that your extension is running asynchronisely with MD.  The user could close the current file and open a new one whilst your extension is active.  This means that any data you have loaded will become invalid as all of the object references have disappeared.  You need to listen for the file being closed and then act accordingly.  In your Main class override the handleEvent method as follows:


```
#!Java

@Override
public void handleEvent(String appEvent) {

    if ("md:file:opened".equals(appEvent)) {
        onAccountSetOpened();
    }

    if ("md:file:closing".equals(appEvent)) {
        onAccountSetClosing();
    }

}
```
(Thanks to Kevin Stembridge for this).

[See](Application Events) for other events that can occur.

## Lists

In 2015 the API has been tidied up.  Where in 2014 the lists returned where a variety of different interfaces all lists of data are returned as Java Lists of a particular type.  For example:


```
#!Java

        	AddressBook lab = objAcctBook.getAddresses();
        	List<AddressBookEntry> listAddresses = lab.getAllEntries();

        	BudgetList lb = objAcctBook.getBudgets();
        	List<Budget> listBudget = lb.getAllBudgets();

         	CurrencyTable lct = objAcctBook.getCurrencies();
         	List<CurrencyType> listCurr = lct.getAllCurrencies();

        	ReportSpecManager lm = objAcctBook.getMemorizedItems();
        	List<ReportSpec> lmgraphs = lm.getAllGraphs();
        	List<ReportSpec> lmreports = lm.getAllReports();

        	ReminderSet ls =objAcctBook.getReminders();
        	List<Reminder> listRem = ls.getAllReminders();

```

## SyncRecord

Another addition to 2015 is the SyncRecord class.  This is not included in the moneydance-dev.jar but some of the classes listed return an instance of this class.  It contains a set of standard <key><value> pairs, for example:


```
#!Java

        	ReportSpec stitem = (ReportSpec) userobj.getobject();
        	SyncRecord srParms = stitem.getReportParameters();
        	Set<String> setKeys =srParms.keySet();
        	String [] keys =setKeys.toArray(new String[setKeys.size()]);

```

Where the documentation specifies a SyncRecord instance as its return type and you wish to manipulate it you will need to reference the main moneydance.jar rather than the moneydance-dev.jar. 

Both the <key> and the <value> are strings but you can set almost any type by using a variation of the put(method).  If you look at the expansion of the class you will see items such as:


```
#!Java

put (String key, String value)
put (String key, int value)
put (String key, double value)
put (String key, long value)
put (String key, Object value)

```

There are the equivalent get() methods for example:


```
#!Java

getString(keyitem,defaultvalue);
getInt(keyitem,defaultvalue);
getLong(keyitem,defaultvalue);

```
These methods do the appropriate conversions and store all keys and values as Strings.