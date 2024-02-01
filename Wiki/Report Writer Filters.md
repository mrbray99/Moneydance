[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Report Writer](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer)>Report Writer Filters

# Filters #

The user can create a set of filters that determine which data is extracted.  These filters can contain any of the following:

* Dates
* Accounts
* Budgets
* Categories
* Currencies
* Transaction Status
* Cheque Numbers
* Transaction Tags
* Securities
* Investment transfer types

A 'From date' and 'To date' must be entered though none of the other filters need to be entered.  If not entered all records will be extracted within the date range entered.  **Note:** not all records have dates and therefore the date filter will not apply. See  ** [Selecting Records](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Filter%20by%20Record) ** to see which filters apply to which records.

If filters are entered each record to be extracted will be tested against each filter and if valid the record is extracted.  If any of the filters are invalid then the record is not extracted.

## Transactions ##

Moneydance splits transactions into a number of records.  The main record is called the Parent Transaction, it contains the main fields such as dates, account, description, value etc.  The other records are called Split Transactions of which there will be at least one.  It will be a reverse of the Parent Transaction and the account will be the category and the value will be the inverse of the entered value.  Effectively this makes Moneydance a double entry finance system.  A few examples are below:

If you have a transaction where the selected account is 'Current', the category 'Car', the value 100.00 and the dates 1/10/2020 the generated records are:

|Num |Type|Account    |Dates   |Description|Value
|----|----|-----------|--------|-----------|------|
|1|Par|Current|1/10/2020|Entered description|-100.00|
|2|Spl|Car|1/10/2020|Entered description|100.00|

Example 2: Selected account 'Current', category 'Car', value 100.00, VAT/GS category 'Sales Tax', VAT/GS % 20, dates 1/10/2020.

|Num |Type|Account    |Dates   |Description|Value
|----|----|-----------|--------|-----------|------|
|1|Par|Current|1/10/2020|Entered description|-100.00|
|2|Spl|Car|1/10/2020|Entered description|80.00|
|3|Spl|Sales Tax|1/10/2020|Entered description|20.00|

Report Writer will output one record per Split Record. In Example 1 one record will be output. It will contain the following fields:

|Field|Value|
|---|---|
|ParentTxnID|Internal ID of the Parent|
|TxnID|Internal ID of the Split|
|AccountName|Name of the account from the Parent|
|CheckNum|Check Number from the Parent|
|DateEntered|The date of the transaction|
|DatePosted|If the transaction was downloaded from a bank, the date it was posted|
|Description|The description from the Parent|
|Status|The status of the transaction from the Parent|
|TaxDate|The accounting date of the transaction|
|Prnt Value|The value of the Parent transaction (-100.00)|
|SpltValue|The value of the Split transaction (100.00)|
|ForAmt|0.0|
|TransferType|xftrp_bank|
|Tags|Tags from Parent|
|Memo|Memo from Parent|
|Category|Category from Split (Car)|
|TransAcct||

In Example 2 one record will be output for each split. They will contain the following fields:

|Field|Value Split 1|Value Split 2|
|---|---|---|
|ParentTxnID|Internal ID of the Parent|Same|
|TxnID|Internal ID of Split 1|Internal ID of Split 2|
|AccountName|Name of the account from the Parent|Same|
|CheckNum|Check Number from the Parent|Same|
|DateEntered|The date of the transaction|Same|
|DatePosted|If the transaction was downloaded from a bank, the date it was posted|Same|
|Description|The description from the Parent|Same|
|Status|The status of the transaction from the Parent|Same|
|TaxDate|The accounting date of the transaction|Same|
|Prnt Value|The value of the Parent transaction (-100.00)|0.0 (so not to duplicate the Parent value)|
|SpltValue|The value of the Split transaction (80.00)|The value of the Split transaction (20.00)
|ForAmt|0.0|Same|
|TransferType|xftrp_bank|Same|
|Tags|Tags from Parent|Same|
|Memo|Memo from Parent|Same|
|Category|Category from Split (Car)|Category from Split (Sales Tax)|
|TransAcct|||

Example 3: Investment Transactions are slightly different.  When you conduct a buy or sell of a security the transactions created are slightly different.  For example a sell of 100 shares at a price of £10 of Security Apple in Account Investment with a fee of £5. The records are 

|num|Type|Account     |Transfer Type|Description|Value  |Currency|
|---|----|------------|-------------|-----------|-------|--------|
|1|Par|Investment|xfrtp_buysell|Entered description|995.00|GBP|
|2|Spl|Bank Charges|xfrtp_buysell|Entered description|5.00|GBP|
|3|Spl|Apple|xfrtp_buysell|Entered description|100|Apple|

**Note** when you input this transaction you choose the category for the fee. In this case it is 'Bank Charges'. Record 3 is the reverse of the transaction in that the value of the transaction is 995.00 + 5.00 (1000.00) and the number of shares sold is 100 (currency Apple) so the price is £10 (£1000/100).  The price is not stored, however Report Writer has specific fields for Investment Transactions. These fields will appear on both the Parent and Split transactions.

Report Writer will output 1 record for each Investment transaction.  It will contain the following fields:

|Field|Value|
|---|---|
|TxnID|Internal ID of the Split|
|AccountName|Name of the account from the Parent|
|CheckNum|Check Number from the Parent|
|DateEntered|The date of the transaction|
|DatePosted|If the transaction was downloaded from a bank, the date it was posted|
|TaxDate|The accounting date of the transaction|
|Curr|The currency of the Account|
|Security|The name of the security if present (Apple)|
|Ticker|The Exchange Ticker of the security if present (APL)|
|Transfer Type|The type of investment transaction (xfrtp_buysell)|
|Description|The description from the Parent|
|Memo|Memo from Parent|
|Status|The status of the transaction from the Parent|
|TransAcct|The other account if this is a transfer to/from the investment account|
|Category|Category from Split (if it has one)|
|NumShares|Number of shares transacted (100)
|Price|The share price (12)|
|Prnt Value|The value of the Parent transaction (-1215.00)|
|SpltValue|The value of the Split transaction (0.00)|
|Fee|The fee paid (5.00)|
|Fee Account|The category for the fee (Bank Charges)|

### Specifying Filters ###

Filters are specified in 'Data Filter Parameters'. More details can be found ** [here](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report Writer Filter Parameters) **

Please refer to the following pages for more information about Report Writer:

* **[Report Writer](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer)**
* **[Report Templates](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Templates)**
* **[Data Filter Parameters](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Filter%20Parameters)**
* **[Data Selection Groups](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Selection%20Groups)**
* **[Reports](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Reports)**