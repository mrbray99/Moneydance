[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>Security Price Load

# Welcome

Welcome to the wiki for the Moneydance Security Price/History Load Extensions.   These extensions were originally built to load data from Hargreaves Landsdowne.  In November 2017 a major change was done to accommodate input from Yahoo Finance.  If you want to load data from Yahoo you will need to create a Yahoo account and create a list of securities/currencies you wish to watch.  Yahoo will update the prices daily. To load this data download your list of quotes to a .csv file.  Lastly there is a third way of using these extensions using the hleofxquotes program to create the .csv file.

## What do the extensions do

The extensions load a .csv file containing security and currency prices and allows the user to select which prices to update. Security Price Load is aimed at loading current prices.  It can only have one entry per Ticker/Currency.  The Security History Load extension can have several entries per Ticker/Currency but they must have different dates.

**Note:** It is worth understanding MoneyDance before using these extensions.

MoneyDance holds all values in the base currency of the file, even if you display your data in a different currency.  If you change/enter historical currency rates MoneyDance will recalculate any historical security data.  Though this will not change the current price the historical data will look strange.  Use with caution.

I suggest you load the file, click on any Currency prices and save those, then click on the security prices and save those.

## Installation

These extensions are provided in a normal Moneydance extension file (.mxt)

* Download the securitypriceload.mxt or securityhistoryload.mxt file from the \downloads folder in this repository.
* Use the Moneydance Extension->Manage Extensions->Add From File to add the extension


## Use

This wiki has an overview of how to use these extensions. Start with the [Format](https://bitbucket.org/mikerb/moneydance-2019/wiki/Security Price Load Format)