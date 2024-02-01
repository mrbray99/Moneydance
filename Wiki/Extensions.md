[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>Extensions
# Moneydance Extensions Developed by Mike Bray

A number of Extensions have been developed to help with understanding Moneydance and adding features not readily available in the standard product.

THESE EXTENSIONS HAVE BEEN TESTED BUT YOU USE AT YOUR OWN RISK.  THE FUNCTIONAL TESTING HAS BEEN DONE ON WINDOWS. EACH EXTENSION HAS BEEN INSTALLED AND RUN ON MAC OSX AND DEBIAN LINUX.

The information presented here is relevant for Moneydance 2019 only.  If you are running Moneydance 2015/2017 refer to the repository [Moneydance 2015](https://bitbucket.org/mikerb/moneydance-2015/wiki) for more information.

**WARNING FROM MONEYDANCE BUILD 1899 THE INFINITE KIND HAS INTRODUCED STRICTER SECURITY FOR THE USE OF THIRD PARTY EXTENSIONS.  A NUMBER OF CHANGES HAVE BEEN MADE TO FIT IN WITH THIS.  NOTABLY THE MB INSTALLER EXTENSION HAS BEEN RETIRED AND QUOTE LOADER HAS BEEN RE-ENGINEERED TO USE A DIFFERENT BACKEND FOR RETRIEVING PRICES**

The extensions available are:

1. A [Budget Generator](https://bitbucket.org/mikerb/moneydance-2019/wiki/Budget Gen) - the 'New' format budgets within Moneydance have prescribed periods and start dates.  This extension allows you to enter different cost patterns and then generate the appropriate budget entries within Moneydance. Latest build 3009.

2. [File Display](https://bitbucket.org/mikerb/moneydance-2019/wiki/File Display) - if you are developing extensions for Moneydance and you wish to understand the contents of your file, this extension displays the contents of the current file. Latest Build 3036.

3. [Security Price/History Load](https://bitbucket.org/mikerb/moneydance-2019/wiki/Security Price Load) - loads security prices from a .csv file. There are two extensions. One aimed at loading current prices and one aimed at loading historical prices. Latest Builds 3036.

4. [Security Transaction Load](Security Transaction Load) - loads transactions from investment and security accounts using a .csv file. Latest Build 3036.

5. [Budget Report](Budget Report) - reports on budget verses actual with the ability to roll-up actuals into parent categories. Latest Build 3036.

6. [Reporting](Reporting) - a generic utility that is used for all reports

7. [Quote Loader](Quote Loader) - captures new security and currency prices from FT.com or Yahoo.com. Latest Build 3037.

8. [QIF Loader](QIF Loader) - loads a file of transactions in QIF format, allows the user to switch negative and positive values and then save to Moneydance. **Note:** This extension has not been verified by TIK. Latest Build 3027.

9. [Report Writer](Report Writer) - a general reporting and data export extension.  This extension can out put selected Moneydance data into a Spreadsheet, a set of .csv files or an h2 database.  You can also run the database against a report template3 created using the JasperSoft Jasper Studio.

## Upgrading

If you are upgrading from Moneydance 2017 to Moneydance 2019 and have used my extensions in the past you need to update all extensions to the 2019 version.  Your parameters will be saved, however, the preferences will need to be reset.

## Preferences

Each Extension stores preferences in a file in the data directory of the current Moneydance file.  This file is called **MRBPreferences.dict2**.  This file is mainly used for remembering window sizes.  If you change the size of the extension window the size will be remembered. You do not need to Save the preferences.

## Parameter Files

In addition to the Preferences certain extensions store parameters in other files.  This data needs to be saved by the user.  Each extension that uses such a file will have a 'Save Parameters' button. See [Parameters](https://bitbucket.org/mikerb/moneydance-2019/wiki/Parameters) for the names of the various files created.

## Download

**If you were a user of the MB Installer (extinstaller.mxt) it has been retired.  Please use the MD Manage Extensions menu choice to remove it**

**IMPORTANT: The Quote Loader extension is now a single application.  You should remove the Rhumba extesion as above.  If you wish to tidy up your installation. Close MD after you have removed Rhumba and delete the folder .moneydance/fmodules/.rhumba. This is not a requirement**

The Quote Loader extension is now available from The Infinite Kind via the 'Manage Extension' tab under Extensions.  Alternatively you can download it from this site as set out below.

To install any extension please do the following:

1. Download the chosen extension from the  ([Downloads folder](https://bitbucket.org/mikerb/moneydance-2019/downloads/)). **Note:** There are some old versions of Quote Loader in this directory, the current one is 'securityquoteload.mxt'. If you wish to install an earlier version download it and change the name to 'securityquoteload.mxt'.
2. Start Moneydance
3. Select Extensions/Manage Extensions from the menu
4. Click on Add From File
5. Select the file you have downloaded
6. You will be presented with a screen describing the extension and its build number.  If this is what you expected click on Install Extension.

**Note: It is important that if you install the Quote Loader extension you will need to restart MD after installing it.**