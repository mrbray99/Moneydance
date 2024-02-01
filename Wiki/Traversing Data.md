[Home](Home)>[Moneydance](Moneydance Information)>Traversing Data

# Traversing the Data Model

## Context

When Moneydance starts up it creates an 'Application Context' for extensions.  This is effectively the connection between extensions and the underlying data (objects).

You gain access to the context by calling

```
#!java


FeatureModuleContext context = getContext();
```


The  Moneydance 'database' is accessed via objects. Moneydance takes care of obtaining the data from the file system and presenting it as a series of instances of the Moneydance classes.

You traverse the data by calling the appropriate methods within the 'context' to gain access to the first level and then you traverse the data model by accessing objects and calling the appropriate methods (see [Data Model](Moneydance Object Model) for the heirarchy).

In 2014 all data was accessed via the RootAccount.  In 2015 only the Account heirarchy is accessed via the RootAccount.  The rest of the data is accessed via the AccountBook.

## RootAccount

Each Account and Category, within Moneydance, is held in an Account object. T There is one special Account called the RootAccount.  This is the owner of account data within Moneydance and to gain access to that data you need to have access to the RootAccount.

This is done by calling:

```
#!java


Rootaccount root = getUnprotectedContext().getRootAccount();
```
## AccountBook

The current AccountBook is no longer contained within an AccountBookModel.  In fact you should not try and access any AccountBook other than the current one.  To gain access to the current AccountBook use:


```
#!Java

	    objAcctBook = extension.getUnprotectedContext().getCurrentAccountBook();

```

## Accounts and Categories

As mentioned accounts and categories are the same, they are all Accounts.  With 2015 a nested class called Account.AccountType has been introduced.  This is an enumeration and it determines what type of Account it is.  

Accounts are held in a heirarchy, i.e.  Each Account can have 0 or more subaccounts but only 1 parent account.  You traverse the accounts by getting a list of accounts for a particular Account and then iterating for each account in the list, e.g:

![moneydanceaccounts.jpg](https://bitbucket.org/repo/4oKeEz/images/3155413286-moneydanceaccounts.jpg)

You use *getSubAccountCount()* to tell you how many subaccounts there are and then you can run through each account using *getSubAccount(i)*.

eg.
	  * 
```
#!Java

 RootAccount root = this.getUnprotectedContext().getRootAccount();
	     int sz = root.getSubAccountCount();
	     for(int i=0; i<sz; i++) {
	       Account acct = root.getSubAccount(i);
	       accounttype = acct.getAccountType();
               List<Account> l = accounts.get(accounttype);
               if (l == null)
                 accounts.put(accounttype, l=new ArrayList<Account>());
               l.add(acct);
             }
```

This creates a list of accounts at the highest level by AccountType, i.e those below the RootAccount.

You can then take each of those accounts and repeat the process, e.g in the above diagram you loop through Account1, Account2 up to Accountn.  For Account1 it will give you a subaccount count of 2.

## Budgets

When you create a Budget using Budget Manager a BudgetList object is created plus 1 or more Budget objects.

When you enter a value in your budget using the UI it creates a BudgetItem object for that account and that period.

You can gain access to Budget data from the AccountBook:


```
#!Java

        	BudgetList lb = objAcctBook.getBudgets();
        	List<Budget> listBudget = lb.getAllBudgets();
     		int lbc = listBudget.size();
     		if (lbc > 0) {
     			for (int i=0;i<lbc;i++) {
       				myNodeObject mynode = new myNodeObject (EXTRAS_BUDGET_ENTRY,listBudget.get(i).getName());
    				mynode.setobject(listBudget.get(i));
    	        	dataitem = new DefaultMutableTreeNode(mynode);
    				node.add(dataitem);  				
     			}
     		}    		
```

## Address Book

There is one AddressBook and 1 or more AddressEntry.

You access the data from the AccountBook:

```
#!java

        	AddressBook lab = objAcctBook.getAddresses();
        	List<AddressBookEntry> listAddresses = lab.getAllEntries();
     		int labc = listAddresses.size();
     		if (labc > 0) {
     			for (int i=0;i<labc;i++) {
       				myNodeObject mynode = new myNodeObject (EXTRAS_ADDRESS_ENTRY,listAddresses.get(i).getName());
    				mynode.setobject(listAddresses.get(i));
    	        	dataitem = new DefaultMutableTreeNode(mynode);
    				node.add(dataitem);  				
     			}
     		}    

```



## Currencies

The currencies within Moneydance are held within a CurrencyTable.  This will be populated with all predefined currencies, not just the ones you have selected.  In addition there are entries for securities.  If one of your investment accounts holds shares for say, IBM, there will be a currency for IBM.  The account will have a balance of the number of shares and a currency of IBM.

The data is accessed via theAccountBook, it is held as an array of CurrencyType.  There is a nested class called CurrencyType.Type that determines whether the CurrencyType is a currency or a security.


```
#!java
        	CurrencyTable lct = objAcctBook.getCurrencies();
         	List<CurrencyType> listCurr = lct.getAllCurrencies();
     		long lctc = lct.getCurrencyCount();
     		if (node.isLeaf()) {
	     		if (lctc > 0) {
	     			for (int i=0;i<lctc;i++) {
	     				if (listCurr.get(i).getCurrencyType() == CurrencyType.Type.CURRENCY) {
	     					myNodeObject mynode = new myNodeObject (EXTRAS_CURRENCY_ENTRY,listCurr.get(i).getName());
	     					mynode.setobject(listCurr.get(i));
	     					dataitem = new DefaultMutableTreeNode(mynode);
	     					node.add(dataitem);
	     				}
	     			}
	     		}
     		}	
  
```
## Reminders

Reminders are held as a ReminderSet. The set does not exist until there is at least one Reminder.

The data is accessed via the AccountBook, it is held as a list of Reminder:


```
#!java

       	ReminderSet ls =objAcctBook.getReminders();
        	List<Reminder> listRem = ls.getAllReminders();
        	long lsc = listRem.size();
     		if (lsc > 0) {
     			for (int i=0;i<lsc;i++) {
       				myNodeObject mynode = new myNodeObject (EXTRAS_REMINDER_ENTRY,listRem.get(i).getDescription());
    				mynode.setobject(listRem.get(i));
    	        	dataitem = new DefaultMutableTreeNode(mynode);
    				node.add(dataitem);  				
     			}
     		}    		
	

```
## Memorized Items

There are two types of memorized items.  Graphs and Reports.  With 2015 these are all held within a ReportSpecManager.  This not only includes the items you have memorized but also the standard reports, though you can get a list of just the memorized items. There is a separate list for each type.  

The data is accessed via the AccountBook, each type is held as a List of ReportSpec:

```
#!Java

        		// list memorized items
        	ReportSpecManager lm = objAcctBook.getMemorizedItems();
        	List<ReportSpec> lmgraphs = lm.getAllGraphs(); // use getMemorizedGraphs for sublist
        	List<ReportSpec> lmreports = lm.getAllReports();// use getMemorizedReports for sublist
        	/*
        	 * two types of memorized items, Graphs and Reports
        	 */
        	int lmcgraphs = lmgraphs.size();
        	int lmcreports = lmreports.size();
     		if (lmcgraphs > 0) {
     			/*
     			 * add graphs to the tree
     			 */
     			for (int i=0;i<lmcgraphs;i++) {
     				if (lmgraphs.get(i).isMemorized()) {
	       				myNodeObject mynode = new myNodeObject (EXTRAS_MEMORIZED_GRAPH_ENTRY,lmgraphs.get(i).getName());
	       				mynode.setobject(lmgraphs.get(i));
	    	        	dataitem = new DefaultMutableTreeNode(mynode);
	    				node.add(dataitem);  				
     				}
     			}
     		}    		
     		if (lmcreports > 0) {
     			/*
     			 * add reports to the tree
     			 */
     			for (int i=0;i<lmcreports;i++) {
     				if (lmreports.get(i).isMemorized()) {
	       				myNodeObject mynode = new myNodeObject (EXTRAS_MEMORIZED_REPORT_ENTRY,lmreports.get(i).getName());
	       				mynode.setobject(lmreports.get(i));
	    	        	dataitem = new DefaultMutableTreeNode(mynode);
	    				node.add(dataitem);
     				}
     			}
     		}    		
```

## Transactions

Transactions are accessed from the RootAccount.  They are held in a TransactionSet.  To gain access to them use:

```
#!Java

    	TransactionSet  txnSet = root.getTransactionSet();

```
You can access the transactions in a number of ways, either as a Enumeration or as a TxnSet. Transactions can be obtained for the whole file or by Account  No matter which way you access them the base class is AbstractTxn.  This is not an object, it is abstraction.  A transaction will either be a ParentTxn or a SplitTxn.  You determine what type of transaction it is using the *Instanceof* operator.

For example:

```
#!java

    	TransactionSet  txnSet = root.getTransactionSet();
        TxnSet e;
  	e = txnSet.getAllTxns();
    	for (int i=0;i<e.getSize();i++) {
    		AbstractTxn txn = e.getTxn(i);
    		AbstractTxn txn2 = null;
    		Vector<String> vec = new Vector<String>();
    		vec.add(new DecimalFormat("#").format(txn.getTxnId())+" ");
    		if (txn.getAccount() == null) {
    			vec.add("None");
    			ctype = ctgbp;
    		}
    		else {
    			vec.add(txn.getAccount().getAccountName());
    			ctype = txn.getAccount().getCurrencyType();
    		}
    		vec.add(txn.getDescription());
    		vec.add(cdate.format(txn.getDateInt()));
    		vec.add(ctype.formatFancy(txn.getValue(),'.'));
    		if (txn instanceof ParentTxn)
    			vec.add("Parent");
    		else
    			vec.add("Split");
    		tranmodel.addRow(vec);
    	}

```
