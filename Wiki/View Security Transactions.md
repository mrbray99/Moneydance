[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Security Transaction Load](Security Transaction Load)>View Security Transactions

## View Security Transactions

This screen shows the loaded transactions from the .csv file and it looks like this:

![securitytranload5label.jpg](https://bitbucket.org/repo/K6egeG/images/718934350-securitytranload5label.jpg)

Column (1) is the Ticker.  If it has a red background then the Ticker does not match a Ticker on a Security for the chosen Investment Account.

Column (2) is the date of the transaction.  If this is Green then the transaction has already been processed.  This is based on Ticker, date, amount and Transaction Type.

Column (3) is the cleared status of the transaction if it has already been loaded

Column (4) is the Transaction Type.  If this is yellow there is a record in the file that does not match any of the Transaction Types.

Only rows that have no colours can be selected (5).  You can select all available rows by clicking the box at the bottom (6).

Once you have selected the rows to process click on **Generate Transactions** (7).

[Prev](https://bitbucket.org/mikerb/moneydance-2019/wiki/Security Transaction Load Introduction)-[Next](Generate Transactions)