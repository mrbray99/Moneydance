[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Security Price Load](Security Price Load)>Security Price Load Format

## File Format

The file must be a comma separated values file with an extension of .csv.  This file must have a header row with the field names in the first row.  Each column header must be separated by a comma and not have any commas in the text.

The file can contain entries for Securities and/or Currencies.  Each row in the file must have a 'Ticker' and a price.  For the History Load extension it must also contain a date which is the date that the price was set.

The file can contain as many columns as you like but it must contain at least two columns.  One with the code of the security and one with the new price.  In the Security History Load extension there must be a third field containing the date of the price.

Prices can have commas in them but the price with a comma must be deliminated by "".

e.g

```
#!Java

Code,Stock,Units held,Price (p),Value (£),Cost (£),G/L (£),G/L (%)
0387510,Fidelity Special Situations Inclusive - Class A - Accumulation (GBP),63.91,"2,749.00","1,756.89","1,489.25",267.64,17.97
```
##Securities

For Securities the Ticker codes can have the Exchange added to them.  The separator can be either a period (.) or a colon (:).  If you wish you can have the extension remove the Exchange before matching with the Moneydance security accounts.

As some exchanges provide the prices in different denominations it is possible to provide an Exchange Multiplier that will adjust the price to match Moneydance.  This multiplier can be different for each exchange, no matter whether or not you remove it from the Ticker.

**Note:** If your ticker contains a period (.) or colon (:) and you wish not to remove the Exchange then you will not be able to use the Exchange Multiplier.  For example: IFFF:LSE:GBX.  The extensions will treat the IFFF as the ticker and LSE:GBX as the exchange.

In the file a Security/Fund entry has a 'Ticker' which is known as an ISIN code.  This can be in a variety of formats.  With USA Equities it is just the Ticker, eg: MSFT for Microsoft. For UK the exchange is added. eg: BKG.L.  For UK EFTs the ticker is prefixed with GB00 or IE00.

This extension will allow you to remove these before it compares the Ticker with Moneydance.

Lastly the number of characters in the Ticker might be longer than that stored in Moneydance.  The extension allows to specify how many characters to compare.  This can be restricted to 6,7,8 or 9 characters.

Moneydance stores all Securities in a separate table.  If you add a Security to an Investment account a new Security Account is created.  If you register buys against that Security then you will have a non-zero account.  To load prices against a Security it should be within an Investment Account and have a holding, i.e. non-zero.  If you wish to load prices for other Securities you need to 'Include Zero Accounts'.  This will load any Security that has the 'Show on Summary Page' option ticked.  If this is not ticked you can not load a price using these extensions.

##Currencies

Currency exchange rates must be in the following format **fffttt=X** where **fff** is the from currency and **ttt** is the to currency, eg.  **GBPUSD=X**.  This extension will look for the '=X' at the end and treat the line like a currency.

Moneydance stores exchange rates for all currencies with the base currency, i.e if the base currency is USD then only rates that start with USD will be stored.  If your base currency is GBP then only lines in the format GBPttt=X are loaded.

**Note:** Moneydance does not store exchange rates for all combinations of currencies, these are calculated from the base currency exchange rates.  For example if your base currency is USD and you want the GBP to EUR exchange rate Moneydance will use the USD to GBP and the USD to EUR to calculate the GBP to EUR rate.  These extensions therefore only load the base currency rates.

Lastly.  This extension will only deal with currencies that do not have the 'Hide on Summary' flag set.

The next page gives more information: [Introduction](https://bitbucket.org/mikerb/moneydance-2019/wiki/Security Price Load Introduction)