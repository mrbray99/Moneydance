Introduction

# Welcome to the Wiki of Mike Bray

This Wiki has been created to pass on information, code and opinions.  I hope you find it useful.

Moneydance 2019 is the new version of the popular finance package.  For previous versions please refer to the Wiki for [Moneydance 2015](https://bitbucket.org/mikerb/moneydance-2019/wiki). The operation of 2019 is not dissimilar to 2015/7, however there are a number of changes which not only improve the user interface but also the installation on Windows and interoperability via python.  

If you are upgrading on Windows you might want to read [this](Windows Installation).

The file structure of 2019 is the same as 2017, however, there have been some changes in the way currency and security prices are stored.  Once you have opened a file in 2019 it is inadvisable to open it in 2017.

The Wiki/site is split into a number of different areas:

1. Information about the Moneydance Personal Finance package (see [Moneydance](Moneydance Information))
2. Extensions for Moneydance developed by Mike Bray (see [Extensions](http://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions))

### Update History ###
Build 3037 25/05/2021 - NOT Verified by TIK

* Quote Loader - added % changed column to new price, bug fixes

Build 3036 02/05/2021- Verified by TIK except QIF Loader

* Quote Loader - corrected issue with volume in csv export file. Introduced in build 3033.

Build 3035 21/04/2021- NOT Verified by TIK - uploaded to Downloads Folder

* All Extensions - fixed second error in parameters, was not remembering changes
* Report Writer - Added intro screen to help users who use the extension without reading the Wiki

Build 3034 16/04/2021 - Verified by TIK

* All extensions - fixed error in parameters, using more than one extension in same run overwrote parameters
* Report Writer - fixed issue with first run parameter setting

Build 3033 12/04/2021 - NOT Verified by TIK

* File Display - fixed issue created by new preview change of Look and Feel
* Report Writer - fixed issues around zip files
* Quote Loader - added high and low prices to Yahoo History

Build 3031 09/04/2021 - Verified by TIK

* Quote Loader - fixed issue with setting the current price, this was being overwritten by any transaction entered in the same day.
* Report Writer - fixed 2 issues. 1. Transfer acct on investment trans was being set wrong. 2. Price decimal places were wrong on securities with greater than 4 decimal places. (unverified)

Build 3030 02/04/2021

* Report Writer - added Parent Category and Category Full Name to transaction, added Amount to Investment Transaction.  Normally Amount matches Parent Value. On dividend transfers the parent value is 0.0 but the Amount field has the dividend amount.

Build 3029 28/2/2021

* Quote Loader - fixed bug when blank line ticked

Build 3028 29/01/2021 

* First Beta release of Report Writer

Build 3027 6/12/2020 - Verified by TIK

* All extensions updated debugging process

* Quote Loader - Allowed & in ticker symbols, overcome issue created by TIK adding Locales to latest build

Build 3026 3/7/2020 - Verified by TIK

* Quote Loader added retrieval of historical currency rates from Yahoo.

Build 3025 11/06/2020 - Verified by TIK

* Quote Loader added feature to retrieve historical prices from Yahoo and FT

Build 3024 28/03/2020 - Verified by TIK

* Quote Loader now deals with indices on Yahoo (^xxx), slight change to have the exchange column works.

Build 3023 08/02/2020 - Verified by TIK

* Quote Loader added GBp to pseudo currencies for Yahoo/

Build 3021 01/02/2020 - Verified by TIK

* MB Installer retired due to change in MD security
* Quote Loader re-architected to eliminate Rhumba and hleofxquotes.jar, new backend added.  Change to button names on Exporting prices.

Build 3018 08/12/19

* Security Transaction Loader - Introduced use of new way of creating investment transactions, no change in functionality

Build 3017 06/12/19

* Security Transaction Loader - correct generation of transactions, remember last directory

Build 3016 02/08/19

* Quote Loader - Right align fields.

Build 3015 01/08/19

* Quote Loader - change all tickers to upper case when loading.

Build 3014 20/6/19

* Quote Loader - added 2 and 3 as options for decimal places.  Made change to storage of security prices, now rounded to chosen decimal places.

Build 3013 31/5/19

* Quote Loader - fix bug in autorun when MD left running over night

Build 3012 10/5/19

* Security Transaction Loader - bug fixes on creation of transactions created by changes to MD

Build 3011 24/4/19

* QIF Loader - updated for UNIX.

Build 3010 23/4/19

* QIF Loader - introduced

Build 3009 27/2/19

* Quote Loader - cater for different font sizes

Build 3008 02/2/19 

* Quote Loader - bug fix for unix 

Build 3007 31/1/19

* Quote Loader introduced zip file

Build 3006 30/1/19

* Quote Loader - added Yahoo HD as a source, corrected check boxes on Linux

Build 3005 17/1/19

* Quote Loader - added export file for prices

Build 3004 8/1/19

* All extensions - set the minimum build number to 1820 to prevent extension being run on earlier versions of Moneydance

Build 3003 3/1/19

* Budget Generator - change error messages to a single message at the end of load
* Quote Loader - expanded next run date, clear 'Select All' flag once prices loaded

Build 3002 28/12/18

* Quote Loader - set table font colours to default for theme

Build 3001 21/12/2018

* Security Price Load, Security History Load, Quote Loader - updates to price storage using new 2019 features.

Build 3000 19/12/2018

* All extensions - transferred to Moneydance 2019.