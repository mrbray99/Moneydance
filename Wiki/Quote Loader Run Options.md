[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Quote Loader](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader)>Run Options

The Quote Loader can run in Manual Mode or Automatic Mode.

In Manual Mode the user controls when the prices are obtained by clicking on the Get Exchange Rates or Get Prices buttons.

In Automatic Mode the program is run on specified dates.  Each time MoneyDance is opened the program will check to see if a run is required.  If the Next Run Date is less than or equal to today's date and the last run date is less than today the program will be run.  It will go through your securities and/or currencies and obtain prices using the saved parameters.  Please note that if you have not set up and saved the parameters this will not do anything. If you wish to understand the way Quote Loader handles these parameters please read [This](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader Autorun).

To control the run type use the options at the top of the screen, which look like this:

![quoteloadlabeltop30202.jpg](https://bitbucket.org/repo/9p4r4rA/images/2373644778-quoteloadlabeltop30202.jpg)

There are two settings that need to be set up for automatic running.

First you need to set what time of day the auto run will occur(1).  It can be one of:

* At Startup - the autorun will occur when you start Moneydance.
* At 09:00
* At 11:00
* At 13:00
* At 15:00
* At 17:00
* At 19:00
* At 21:00
* At 22:00


Once you have determined the time to make the auto run you need to select the frequency.

There are different run schedules for Securities and Currencies.  You select the type of run you want using the drop down lists (2).

The available run types are:

* Manual - do not run
* Daily - runs once every day 
* Weekly - runs on a specific day of the week
* Monthly - runs once a month on a specific day
* Quarterly - runs once a quarter on a specific day. The quarter is a calendar quarter
* Yearly - runs once a year on a specific date

## Daily

If you select Daily you will be presented with this message:

![quoteloademptydaily.jpg](https://bitbucket.org/repo/K6egeG/images/1162042978-quoteloademptydaily.jpg)

Just change the Next date (5) to the date of your first run.  The program will run daily from then on.  If you miss a day then it will run when MoneyDance is opened and then daily from then on.

## Weekly

If you select Weekly you will be presented with this screen:

![quoteloademptyweekly.jpg](https://bitbucket.org/repo/K6egeG/images/3997935384-quoteloademptyweekly.jpg)

Select which day you wish the program to run.  If you miss that day then the program will run the next time you open MoneyDance.  It will then revert to the weekly schedule.

## Monthly

If you select Monthly you will be presented with this screen:

![quoteloademptymonthly.jpg](https://bitbucket.org/repo/K6egeG/images/407086225-quoteloademptymonthly.jpg)

The options available to you are:

* First Monday - this is the first Monday within the month on or after the 1st
* Last Friday - this is the last Friday within the month before or on the last day of the month
* Last Day - this is the last day of the month
* Specific day - the day you wish it to run.  If you set it to a day greater than the number of days in the month then it reverts to the last day of the month.  For example if set to 31st in April it will run on 30th.

If you miss the day it will run the next time you open MoneyDance and then revert to the schedule.

## Quarterly

If you select Quarterly you will be presented with this screen:

![quoteloademptyquarterly.jpg](https://bitbucket.org/repo/K6egeG/images/3890695972-quoteloademptyquarterly.jpg)

The options available to you are:

* First Day - this is the first day of the quarter, i.e 1st Jan, 1st Apr, 1st Jul and 1st Oct.
* Last Day - this is the last day of the quarter, i.e 31st Mar, 30th Jun, 30th Sep and 31st Dec.
* Specific day - the day you wish it to run.  You can choose which Month and Day within the quarter that you wish it to run.  If you set it to a day greater than the number of days in the month then it reverts to the last day of the month.  For example if set to Month 1 day 31st in April it will run on 30th.

If you miss the day it will run the next time you open MoneyDance and then revert to the schedule.

## Yearly

If you select Yearly you will be presented with this message:

![quoteloademptyyearly.jpg](https://bitbucket.org/repo/K6egeG/images/1329794007-quoteloademptyyearly.jpg)

Just change the Next date (5) to the date of your first run.  The program will run each year on the same date. If you miss that day then it will run when MoneyDance is opened and then yearly from then on.

If you wish to change the frequency parameters click on the Calendar icon (3) and the appropriate screen described above will be displayed.

The last date the auto run occurred is shown in field (4).  If you find that the auto run is not occurring when anticipated restart Moneydance to reset the auto runner.

Field (5) shows the next expected run date. 

[Next](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader Selection Criteria)

**NOTE: If you change any of the run options click on the Reset Auto Run button (11).**  This will reset the auto run capability to match the new parameters. **NOTE: If your changes cause Quote Loader to run straight away you will receive a message asking if you wish to close Quote Loader to allow the auto run to proceed.  If you answer no Quote Loader will wait until the next scheduled time.**