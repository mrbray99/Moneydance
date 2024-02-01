[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Quote Loader](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader)>Selection Criteria

The Quote Loader can handle three types of quotations:

1. Securities that are assigned to an Investment Account, for which you have holdings
2. Securities that are defined in your file but you have no holdings
3. Currencies

You select which ones you want to process using the options at the top of the screen:

![quoteloadlabeltop30202.jpg](https://bitbucket.org/repo/9p4r4rA/images/1167687623-quoteloadlabeltop30202.jpg)

By default you will be presented with a list of securities that are assigned to an Investment Account, for which you have holdings.  If you wish to include Securities without any holdings click on 'Include zero accounts' (6).

**Note:** For this to work the Security must have the 'Show on Summary Page' option set.

If you wish to include Currencies click on 'Process Currencies' (7).

**Note:** For this to work the Currency must have the 'Show on Summary Page' option set.

The option (8) allows you to select the number of decimal digits displayed on the prices screen.  The default is 4 though you can choose between 2 and 8.  8 is normally used for Crypto-currencies. **Note:** setting this field will round Security Prices to the appropriate setting.  Exchange Rates are not rounded though they are only displayed to this level.

You also have the facility to create a .csv file of all prices saved.  This file will have the name:


```
#!python

priceexportyyyymmddhhmmss.csv
```
and will be stored in the folder chosen by you (field 10).

There are three times that this file is created:

1. When you save the prices using the 'Save Selected Prices' button
2. When an automatic run has completed
3. When you use the 'Create Prices CSV' button

Options 1 and 2 are set using the 'CSV Settings' menu (field 9), menu items 1 and 2 respectively.

Before any files are created you must set the folder where you wish to save the files.  Use the the third option on the 'CSV Settings' menu (field 9).  You will be presented with a file chooser window.  The chosen folder will appear in field 10.

The file will be in the format:

```
#!python
ticker,name,price,trade date,volume
```

The price will always have a period as the decimal point as the file is a comma separated value file.

[Prev](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader Run Options)-[Next](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader Price Options)