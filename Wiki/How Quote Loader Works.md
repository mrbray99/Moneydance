[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Quote Loader](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader)>How Quote Loader Works

# Introduction #
Quote Loader is an automated way of reading price data from web pages.  It provides access to security prices and currency rates available from ft.com and finance,yahoo.com.  

To determine if Quote Loader supports your security go to either [ft.com](https://markets.ft.com/data/equities/tearsheet/summary?) or [finance.yahoo.com](https://finance.yahoo.com/quote/) and use the provided search features to find your security.  If your security is supported you will be presented with a page with the price and the ticker of that security.  For example Lloyds of London on ft.com is [LLOY:LSE](https://markets.ft.com/data/equities/tearsheet/summary?s=LLOY:LSE) and on finance.yahoo.com is [LLOY.L](https://finance.yahoo.com/quote/LLOY.L).

**If one of your securities is not available on either of these sites, Quote Loader will not be able to load the price.**

Quote Loader loads the current price when you select a Source of FT or Yahoo.

# Historical Prices#

If you do not run Quote Loader every day you will miss certain prices.  Quote Loader can now load missed prices.  However, you must use either FT HD or Yahoo HD as the source.  The tickers for FT HD are the same as FT and the tickers for Yahoo HD are the same as Yahoo.

** ft.com will not allow you to access historical data for Equities.  It does allow you to access historical data for ETFs.  Quote Loader does not know the difference and therefore if you try to load a historical price for an Equity from FT HD it will fail.**

Yahoo HD supports historical prices for both Equities and ETFs.

ft.com provides about a month of prices whereas yahoo.com provides about 1 year of prices.

If you click on the 'Retrieved missed prices' and you are using FT HD and/or Yahoo HD as your sources Quote Loader will load prices from the Last Price date.  For example if you last ran Quote Loader on 27th May and you are now running on 2nd June Quote Loader will load the prices for 28th May, 29th May, 30th May, 31st May, 1st June and 2nd June if they are available.  The prices will be saved when you save the current price.

If you wish to reload the prices for any particular security use the MoneyDance security price history screen to remove all of the entries and then run Quote Loader with the 'Retrieved missed prices' flag set.

** Historical prices are not loaded if you use the source FT or Yahoo.**

[Back](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader)