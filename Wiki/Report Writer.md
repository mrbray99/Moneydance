[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>Report Writer

Welcome to the wiki for the Moneydance Report Writer Extension. This extension is a general purpose data extractor and reporter for Moneydance.  It allows the user to extract data from a Moneydance file and output it to a Comma Separated Values file (.csv), an Excel Spreadsheet (.xlsx) or an h2 database.

In addition the user can run Jasper Reports templates against the h2 database. Jasper Reports is an Open Source general reporting tool available from Tibco JasperSoft.  More information can be found **[here](https://bitbucket.org/mikerb/moneydance-2019/wiki/Jasper%20Reports)**.

The philosophy of this extension is to extract raw data.   It does not do any summarisation of data or grouping.  It is expected that all such work is done on the raw data outside of Moneydance.  For example JasperReport can summarise data and present it in a report.

The data that can be extracted is:

* Accounts
* Addresses
* Budgets
* Transactions
* Investment Transactions
* Investment Lot Allocation Data
* Budget Items
* Securities
* Currencies
* Categories
* Security Prices
* Currency Rates
* Reminders

By default all records are extracted, however, you can apply a number of filters which will restrict the records that will be extracted (see **[Filters](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Filters)**).


# Output Formats #


If the user specifies a Comma Separated Values file (.csv) as the output format a separate file is written for each record type selected in the Data Selection Group.  The first row in each file will contain the field names.

If the user specifies an Excel Spreadsheet as the output format a single file is written containing a Worksheet for each record type selected.  The first row of each Worksheet will contain the field names.

If the user specifies an h2 Database as the output format an SQL table is created for each record type selected with the relevant fields.

If the user selects a Jasper Report template as the output format an h2 Database is created as above.  The Jasper Report template that has been previously created using the JasperSoft Jasper Studio tool is compiled and run against the created h2 Database by this extension.  The output is then run into the Jasper Reports Viewer.

## Fields Output ##

When selecting which record types to output you can select the fields that will be output. **Note:** The fields available are the fields generally available on the Moneydance screens.

Moneydance stores data using internal identifiers and Report Writer does output these identifiers as part of the main record. For example on the Account record the id of that record is output as well as the Account Name.  When another record has a field that references another record the Name of that record is used.  For example transactions contain a field for the Account, the Account Name is output in transaction records.

See **[Fields](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions/Report%20Writer%20Fields)** for a list of fields available.


# Main Screen and Setup #

First you need to download the extension and install it.  The file is called ReportWriter.mxt and it is stored in the Mike Bray Moneydance Download folder (see **[Downloads](https://bitbucket.org/mikerb/moneydance-2019/downloads)**.  Do the following:

1. Click on the file and download it to your computer.
2. Run Moneydance
3. Select Extensions/Manage Extensions
4. Click on Button 'Add From File'
5. Select the downloaded file
6. If Moneydance tells you that the extension is unsigned, don't worry it means that The Infinite Kind has not reviewed the extension.  This extension does not update your Moneydance file. Just allow the install to continue.
7. Once installed click on the 'Installed' tab, Report Writer should be displayed.
8. Exit the Manage Extensions window
9. Restart Moneydance

** Note ** if you are running Moneydance 2021 you will get a red banner highlighting that Report Writer is unsigned.  You can click on Ignore. ** Report Writer does not update any of your data **

The Report Writer should be in the list of extensions, click on Extensions and select it.

When you run the Report Writer for the first time you will be presented with:

![rwsetlabel.jpg](https://bitbucket.org/repo/9p4r4rA/images/3650400953-rwsetlabel.jpg)

You need to configure the folders.

The Report Writer depends on 3 user folders to store data.

### Parameters Folder ###

This folder is used to store the all of the parameters, ie: the Data Filters, the Record Selection groups and the Report Definitions.

### Data Output Folder###

All output files are put in this directory.

### Report Template Folder ###

This folder is used to store the JasperReports templates. If you are using JasperReports you should store the generated Templates in this folder.

Either enter the relevant folders (1), (3), (5) or use the buttons (2), (4), (6) to open a File Selection window.

To help you with understanding the extension there are a number of sample Filters, Selection Groups and Reports available along with a few Jasper Report Templates. These can be downloaded by clicking on button (7).  The files will be copied to the relevant directories.

When you click on button (8) the two files required for Jasper Reports (Moneydance.xml and moneydance.mv.db) will be created. These are used by Jasper Studio.

** Note: ** You can edit these settings at any time by clicking on the Edit Settings button.

## Main Screen ##

The Main screen for Report Writer looks like this:

![rwmainlabel.jpg](https://bitbucket.org/repo/9p4r4rA/images/1850796574-rwmainlabel.jpg)

Screen (1) is a list of JasperReports templates in the Report Directory.

Screen (2) is a list of Data Filter Parameters that you have created in the Data Directory.

Screen (3) is a list of Data Selection Groups that you have created in the Data Directory.

Screen (4) is a list of Records that you have created in the Report Directory.

Use button (5) to exit the extension, button (6) to modify the directories and download the samples.  Button (7) shows the help dialog which gives you access to this Wiki and allows you to change the debug messaging when directed by support.

## Button Images ##

In most cases Report Writer uses Icons on it buttons rather than words.  The main Icons used are:

![edit_node_32px.png](https://bitbucket.org/repo/9p4r4rA/images/3389879034-edit_node_32px.png) - Edit selected record

![icons8-replicate-rows-32.png](https://bitbucket.org/repo/9p4r4rA/images/4052962570-icons8-replicate-rows-32.png) - duplicate row

![delete_node_32px.png](https://bitbucket.org/repo/9p4r4rA/images/971418755-delete_node_32px.png) - delete selected record

![exit_32px.png](https://bitbucket.org/repo/9p4r4rA/images/751378706-exit_32px.png) - close the extension

![print_32px.png](https://bitbucket.org/repo/9p4r4rA/images/1418833526-print_32px.png) - run a Jasper Report

![icons8-spreadsheet-file-32.png](https://bitbucket.org/repo/9p4r4rA/images/457973646-icons8-spreadsheet-file-32.png)- export chosen entry to a Spreadsheet

![icons8-export-csv-32.png](https://bitbucket.org/repo/9p4r4rA/images/40457112-icons8-export-csv-32.png) - export chosen entry to a csv file

![icons8-add-database-32.png](https://bitbucket.org/repo/9p4r4rA/images/95113106-icons8-add-database-32.png) - export chosen entry to a Database

![add_node_32px.png](https://bitbucket.org/repo/9p4r4rA/images/111909263-add_node_32px.png) - add a new record

![001-checked.png](https://bitbucket.org/repo/9p4r4rA/images/3689815131-001-checked.png) - save and close a entry screen

![002-cross.png](https://bitbucket.org/repo/9p4r4rA/images/3662919807-002-cross.png) - cancel the currrent update and close the data entry screen

![settings_32px.png](https://bitbucket.org/repo/9p4r4rA/images/1696693748-settings_32px.png) - edit settings

![help_32px.png](https://bitbucket.org/repo/9p4r4rA/images/53573201-help_32px.png) - display help

# Using Report Writer 

To use Report Writer you need to create a Data Selection Parameter entry, a Data Selection Group and a Reports entry. If you wish to use Jasper Reports you also need to create a Jasper Report template.  The following diagram shows how this works:

![rwflow.jpg](https://bitbucket.org/repo/9p4r4rA/images/2604246857-rwflow.jpg)

The Data Selection Groups determine which Moneydance records will be output.  The Data Filter Parameters determine which of the selected records are output by applying a set of filters.  Finally the Reports bring this all together by selecting the Data Selection Group and the Data Filter Parameter entry to use for a report/output file.  Finally you specify what type of output you want, i.e. whether it is a comma separated values file, an Excel spreadsheet or an h2 database.

You can also specify that you wish to run Jasper Reports against the data by selecting a Jasper Report Template.  If you do this the data is output is written to an h2 database and then Jasper Reports is run against that data using the selected template.  The report is displayed.

You can reuse Data Selection Parameter entries and Data Selection Group entries with several Reports entries.

For example, you can create a Data Filter Parameters entry that just has from and to dates, a Data Selection Group entry that selects all records and three separate Reports entries, one that outputs to .csv file, one that outputs to a Spreadsheet and one to a database. Report Writer can be used without JasperReports.

How to use the Report Writer User Interface is described on the following pages:

* **[Report Templates](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Templates)**
* **[Data Filters](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Filters)**
* **[Data Filter Parameters](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Filter%20Parameters)**
* **[Data Selection Groups](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Selection%20Groups)**
* **[Reports](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Reports)**