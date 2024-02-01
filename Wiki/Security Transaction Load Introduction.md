[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Security Transaction Load](Security Transaction Load)>Security Transaction Load Introduction

## Purpose

Investment accounts in Moneydance have two types of accounts, the Investment Account itself which manages the cash balance and individual Security Accounts which handle an individual Security holdings.  The Security Account manges the value of the holding which is the number of shares times the current price plus any dividends/bonuses/costs.

This extension takes a .csv file of transactions and applies them to either the Investment Account or a Security Account.  Each file can only be applied to a particular Investment Account.

## File Format

The file must be a comma separated values file with an extension of .csv.  This file must have a header row with the field names in the first row.  Each column header must be separated by a comma and not have any commas in the text.

The file can contain as many columns as you like but it must contain the following:

* A transaction code that determines the type of activity on the account
* A Ticker code - the code that must match the Ticker on the Security Account (ignored for Investment Account transactions)
* A date of the transaction
* A value
* A Description

Values can have commas in them but the price with a comma must be deliminated by "". Descriptions can not have commas.

The Ticker codes can have the Exchange added to them.  The separator can be either a period (.) or a colon (:).  If you wish you can have the extension remove the Exchange before matching with the Moneydance security accounts.

## Using the Extension

Select the extension from the Extension Menu.  You will be presented with the following screen:

![securitytransload1label.jpg](https://bitbucket.org/repo/4oKeEz/images/3107727493-securitytransload1label.jpg)

Click on the Choose File icon (1).  This will load the header information from the chosen file into the field selection drop down boxes (3,4,6, 7 and 8).

If you have previously saved the parameters this will also reload them from the file SecurityTransLoad.BPAM in the current Moneydance Document folder. For the first time it will present the following:

![securitytranload2label.jpg](https://bitbucket.org/repo/4oKeEz/images/351571328-securitytranload2label.jpg)

Do the following:

1. Select the columns for the five fields (3,4,6,7 and 8).  All five must be selected and must be different fields.
2. Determine whether or not the extension is to remove the Exchange from the Ticker (5)
3. Add the Transaction codes by clicking on Add Transaction Type (9)

This will allow you to enter one Transaction Type (see [Add](Add Security Transaction Type)).

Once you have defined your transaction types it will look something like this:

![securitytranload4label.jpg](https://bitbucket.org/repo/4oKeEz/images/3435761824-securitytranload4label.jpg)

You can change the Investment Type (13) or Category (14) just by changing the drop down list.  If you wish to change the Transaction Type you need to click on Chg (12).  You can delete a line by clicking on Delete(15).

4. Save the parameters(11).  If you do this the parameters are reloaded next time you use the extension once you have choosen the input file

5. Click on Load Data (10)

[Next](View Security Transactions)