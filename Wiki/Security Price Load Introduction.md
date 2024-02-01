[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Security Price Load](Security Price Load)>[Security Price Load Forward](Security Price Load Forward)>Security Price Load Introduction
## Difference between the two extensions:

This explanation is for the Security Price Load extension.  As the Security History Load extension is basically the same it does not have a separate Wiki page.  The differences between the two extensions are:

1. On the initial screen there is an extra field for the date.  This must be selected.
2. On the Prices screen there isn't an 'As of Date'.  The date on each record is used.
3. On the Prices screen there isn't a flag to update the Current Price.  In the Security History Load extension if the date on the new price record is later than the last date for that security/currency the current price will be updated with the new value.

**Note** When loading historical data (Security History Load) you must load the currency rates first if any of your securities are not in the base currency

## Using the Extension

Select the extension from the Extension Menu.  You will be presented with the following screen:

![securitypriceloadv2022emptylabel.jpg](https://bitbucket.org/repo/K6egeG/images/613388290-securitypriceloadv2022emptylabel.jpg)
Do the following:

1. Click on Load File (1a) or Choose File (1b). You can paste the name of a file into the File Name box and then click on Load File or you can choose a file from a File Explorer window.
**The name of the last file loaded will be remembered, as will the directory where it was found**
2. Select the appropriate file.  This will load the header information and any saved parameters.  The screen will change to:
![securitypriceloadv2022label.jpg](https://bitbucket.org/repo/K6egeG/images/2066412760-securitypriceloadv2022label.jpg)
Then do the following:

1. Select the column that has the Ticker code of the securities (2)
2. Select the column that has the Price of the security (3)
3. Determine whether or not the extension is to remove the Exchange from the Ticker (4)
4. Determine whether or not you wish the Moneydance accounts with no balance should be included (5)  NOte: this will also load Securities that are not assigned to an Investment Account and have the 'Show on Summary Page' option ticked
5. Determine whether or not you wish to process currencies (5a) Note: only currencies that have the 'Show on Summary Page' option ticked will be loaded.
6. Determine whether or not you wish the case of the Ticker to be ignored(6a)
7. If you want the High price, Low Price and/or Daily Volume to be loaded select the appropriate fields (6,7,8).  Note: the High and Low can be the same field as the Price.
8. Select the number of Decimal Places you wish displayed for the price.  This can be 4 to 8 (8a)
9. Enter the number of characters in the ticker to match. (9).  Note: this is after any prefixes have been removed.
10. Enter the multipliers. The **Default** is used when there are no Exchange Level multipliers or the Ticker has an Exchange that is not listed (10)
11. Add any Exchange level multipliers (11).  To Add one click on Add Exchange Multiplier (14) and enter the data in the box.  **Note** do not enter the separator character(. :).
12. If the tickers contain prefixes that are not in Moneydance they can be removed. (12) To add a prefix click on Add Prefix (13).
13. Save the parameters(15).  If you do this the parameters are reloaded next time you use the extension once you have chosen the input file
14. Click on Load Data (16)

The following window is displayed:

![securitydetail2020.jpg](https://bitbucket.org/repo/K6egeG/images/1683094967-securitydetail2020.jpg)

Either select the individual prices you wish to be updated or click on the box at the bottom of the table which will select every security that has a non zero new price.  Click on Save Selected Values and the securities will be updated with the price as of the date at the top of the window.  If you wish a different date change it before clicking the button.

Note the entries at the bottom of the screen.  These are Currencies and operate in the same way as the Securities.

Lastly.  The extension will create a price entry for the date specified at the top of the screen and it will update the current price of the security/currency.  If you uncheck the box above the buttons the current price will not be updated. See above for differences in the Security History Load extension.

You will not be able to click on a security that has a zero new price.