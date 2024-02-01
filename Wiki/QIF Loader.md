[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>QIF Loader
# Welcome

Welcome to the wiki for the Moneydance QIF File Loader Extension

## What does the extension do

This extension will load a set of transactions into a Bank or Credit Card account.  The file must be in QIF format.

On the first screen select the file you wish to load and select the account you wish to add the transactions to.

Click on Load File

You will be presented with list of the found transactions.  The extension can load the following fields:

* Date
* Description
* Amount
* Cheque No
* Category
* Cleared status

** Note ** the category will only be recognised if there is a registered category within MD that matches the category in the file.

** Note ** some credit card companies provide data with the signs reveresed.  Moneydance wants Credit Card charges to be negative and payments to be positive.  If you need to switch the values use the button at the bottom of the screen.

Select the transactions you wish to load.  You can select all by clicking on the box in the bottom left corner.

If you want to switch the values click on 'Switch Values' to reverse the sign on the transactions.

Click on 'Generate Transactions'.

You will be presented with a list of transactions.  There will be two lines per transaction, one with the chosen account and one with the category.  If there is no Category in the file, or it isn't recognised the category used will be the default category of the chosen account.

Select the transactions you wish to save, or click on the box in the bottom left to select all.

Click on 'Save Transactions'.

## Installation

The extension is provided in a normal Moneydance extension file (.mxt)

* Download the qifloader.mxt file from the downloads folder in this repository by clicking  [here](https://bitbucket.org/mikerb/moneydance-2019/downloads)

* Use the Moneydance Extension->Manage Extensions->Add From File to add the extension