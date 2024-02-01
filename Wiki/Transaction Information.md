[Home](Home)>[Moneydance](Moneydance Information)>[Backgound](Background)>Transactions
# Background Information about Transactions

Moneydance is a double entry book keeping system where activity is stored in Transactions.  When you enter an activity (transaction) Moneydance actually creates two or more transactions.  In its basic form there are two transactions one for the **Debit** account and one for the **Credit** account.  In Moneydance the **Categories** are in fact accounts so Transaction 1 identifies the **Account** that is to be affected by the activity and Transaction 2 identifies the **Category**, i.e. type of transaction that is affected by the activity.

For example buying $50 of gas using a credit card would generate:

* Transaction 1: Account (Credit Card), description: Gas, Amount: -$50
* Transaction 2: Category (Gas), Description: Gas, Amount: $50

If you wish to keep track of sales tax separately you can split the category into two.

For example if the above transaction had $4 of sales tax you could get:

* Transaction 1: Account (Credit Card), description: Gas, Amount: -$50
* Transaction 2: Category (Gas), Description: Gas, Amount: $46
* Transaction 3: Category(Sales Tax), Description: Gas, Amount $4

In Transfers you have two transactions:

* Transaction 1: Account sending the money - a debit
* Transaction 2: Account receiving the money - a credit

As you see there is no account of type **Category** involved.

Reports that show amounts against Accounts and/or Categories just total up the transactions with dates within the range of the report.

The Account transaction register shows all of Transaction 1 against the account.  The register will show the Category for Transaction 2 in the Category field.

A report on income and expediture will show all of Transaction 2 and 3 against the relevant categories.

If you use my [File Display](https://bitbucket.org/mikerb/moneydance-2019/wiki/File%20Display) extension you will see the data

[Prev](Accounts Information) - [Next](Budget Information)