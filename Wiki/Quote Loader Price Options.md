[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Quote Loader](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader)>Price and Control Options

Before you run the Quote Loader you need to set up your securities/currencies with the correct Ticker/ID to allow the program to interrogate the chosen Source.

The first thing to decide is which of the three sources you wish to use for each Security/Currency.  You will see a screen like this:

![quoteloadbottomlabel3037.jpg](https://bitbucket.org/repo/9p4r4rA/images/4245942274-quoteloadbottomlabel3037.jpg)

In the Source column (1) select the source from FT (markets.ft.com), FT HD (markets.ft.com), Yahoo (finance.yahoo.com) or Yahoo HD (historical data).  If you wish to set all fields to the same source, right click on the 'Source' heading where you will be presented with a popup menu like this:

![quoteloadsourceright3.JPG.png](https://bitbucket.org/repo/9p4r4rA/images/3620862904-quoteloadsourceright3.JPG.png)

Select the option you want.

**Note** You will be presented with 2 options for each source (FT and FT HD, Yahoo and Yahoo HD).  The first one uses the current price and date and is normally updated in real time.  Sometimes the web site doesn't update the date and this will cause an invalid trade date to be captured.  If you experience this change the source to FT HD or Yahoo HD and Quote Loader will use the last close price from the Yahoo price history page. 

**Note** FT HD does not support shares, it only has historical data for funds.

Once you have determined the source then you need to determine whether or not you require an Exchange (2).  Security Ticker codes are often made up of an ISIN code plus a prefix and/or suffix indicating the Exchange for the quotation.  Unfortunately both FT and Yahoo have different prefix/suffix for the different exchanges.  Therefore you can set the Ticker on the Security to the ISIN code and then tell Quote Loader which Exchange to use.  The Quote Loader will then create a ticker code to use from the Ticker on the security and the prefix/suffix on the Exchange.

To select an Exchange left click on the Exchange field for the chosen line **and then double click the field**.  You will be presented with the following screen:

![quoteloademptyexchange.jpg](https://bitbucket.org/repo/K6egeG/images/4012633054-quoteloademptyexchange.jpg)

Select the required exchange and click on OK.  The exchange code will appear in the Exchange field.


The current list of Exchanges can be found [here](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader Exchanges).  If you wish to see the generated ticker code, **left click on the Exchange field and then right click on it.**  You will be presented with the following popup Menu:

![quoteloadexchangeright.JPG](https://bitbucket.org/repo/K6egeG/images/4169082257-quoteloadexchangeright.JPG)

With this menu you can run a test which will issue a request to the Internet and display a message indicating if this was successful or an error.

You can copy the ticker to the clip board

Or you can copy the chosen exchange to all securities in the table.

If you wish to retrieve missing prices then click on 'Retrieve missed prices' (15).  This only works if the source is FT HD or Yahoo HD.  Also FT HD can not provide missed prices for equities, funds only.  Yahoo HD can provide prices for both. If you successfully retrieve missed prices the Trade Date will have '++' next to it (16).

**Note: Once you have set up your securities/currencies do not forget to save the parameters (10).  This is doubly important if you wish to use the Automatic running feature.**

You can then run the quotes.  You can choose to run the Exchange Rates only by clicking on Get Exchange Rates (3) or run all quotes by clicking on Get Prices (4).

When the Quote Loader is running you will see the screen above.

Initially the colour of the New Price column will be Yellow, it will then turn Green (successful) or Red (unsuccessful) like this:

![priceloadcolours.jpg](https://bitbucket.org/repo/K6egeG/images/1703132035-priceloadcolours.jpg)

You will get a message when all quotes have been run.

You will be given the New Price (5), the % change (6), the Trade Date (7), the Trade Currency(8) and the Volume(9) (if available).  If the Trade Currency does not match the currency of the Security the original quoted price in the Trade Currency will be shown in brackets.  You can edit the New Price and/or Trade Date.  These new values will be loaded into the History record within MoneyDance.

The % change (6) column shows the percentage change between old price and new price (5).  If the change is negative the field will be red.  If the change is positive the field will be green.  If no change it will be grey.

You then need to select which prices you wish to update into MoneyDance.  Either select the individual lines (12) or click on the 'All' option at the bottom of the screen (13).  All lines with a non-zero New Price will be selected. If you want the Volume data to be saved with the price click on 'Include Volume Data' (14). Click on 'Save Selected Values' (17).

Note: Quote Loader only retrieves missed prices since the last 'Price Date'.

Button 18 will export the selected prices to a .csv file (see [Selection Criteria](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader Selection Criteria)).  **Note:** You must have selected the required prices before hand.  You do not have to save the prices.  If you use this and have 'Export on Save' set you will get 2 files with slightly different times.

Button (19) will close the program.

[Prev](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader Selection Criteria)-[Next](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader Exchanges)