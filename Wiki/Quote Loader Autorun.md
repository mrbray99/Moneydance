[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Quote Loader](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote Loader)>[Run Options](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote%20Loader%20Run%20Options)>Autorun Processing

# How does Autorun Work? #

Quote Loader can run automatically. When Moneydance is opened Quote Loader is loaded along with all other extensions, though none of the extensions are displayed.  As part of the loading process Quote Loader determines if there are any Autorun parameters. If so Quote Loader starts a timer to start an automatic run at the desired time.

When the timer expires Quote Loader will run through its parameters and execute a quote request for each security and/or currency with saved parameters.  Any returned results are automatically saved.

## Where are auto run parameters saved?##

All extensions written by me save information into a parameter file. This is called MRBPreferences2.dict and it is stored in the directory holding your data file, so the the auto run parameters are specific to the data file that is open.

## How is the length of the timer determined?##

Quote Loader stores the date and time of the last and next automatic run so when you open a data file Quote Loader determines the time between now and the next run.  If the next run date/time has not been set Quote Loader uses the Auto parameters to determine the next run date/time.

If you close Moneydance the timer is halted and reset when you next open Moneydance.

If the calculated or stored Next Run Date is less than now an Auto run is executed immediately.

## What is the 'Reset Auto Run' button?##

If you open Quote Loader and change the Auto Run parameters Quote Loader will still have a timer based on the old values. Clicking on this button will stop the timer, recalculate the next run date/time and start a new timer.

**Note: Clicking on this button might cause an Auto run to execute immediately.  You will receive a message saying.'Quote Loader is trying to run an Automatic update.  Do you wish to close this window to allow it to run'**

[Back](https://bitbucket.org/mikerb/moneydance-2019/wiki/Quote%20Loader%20Run%20Options)