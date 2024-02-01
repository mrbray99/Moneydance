[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Report Writer](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer)>Report Writer Report Definitions

Report Definitions bring everything together.  You select the Data Selection Group to specify which records will be output in this report, the Data Filter Parameters to specify which of the chosen records will be output, and the type of output.

You manage reports using the Main Screen as shown below:

![reportwritermainrep.jpg](https://bitbucket.org/repo/9p4r4rA/images/4135611730-reportwritermainrep.jpg)

You can Add (1), Edit (2), Copy(3), Delete (4) or Run (5) a report.  You must select an entry before you can edit, delete or run it. Button (6) opens the output folder to allow you to manage the created file.

When you add or edit a report you will be presented with the following screen.

![rwreplabel.jpg](https://bitbucket.org/repo/9p4r4rA/images/3462233933-rwreplabel.jpg)

You must select a name (1), if the name already exists you will be asked if you wish to overwrite that entry.

You then need to select what type of output you want:

* View Jasper Report (2) outputs all of the selected data to an h2 database and then uses it as input to the selected Jasper Template (10)
* Create Database (3) as with View Jasper Report but it does not run Jasper
* Create Spreadsheet (4) creates Microsoft Excel Spreadsheet with an extension of xlsx.
* Create .CSV File(5) creates a separate file for each record, each file will be named as defined (6,7,8 and 9) plus a short record name and the extension .csv (for example outputfileacct.csv)

The next set of fields define the output file.  You can enter a name (6) or choose to have the extension generate a name(7).  This will be called 'ReportWriteryyyy-MM-ddHHmmss' where yyyy is the year, MM the month, dd the day, HH the hour, mm the minutes and ss the seconds.

If you donot generate the name (7) and the file already exists you can choose to overwrite it by clicking on (8), alternatively you can add the Date Stamp (9) which will append the date stamp (as above) to the file name.  ** note ** if you select Generate (7) and Add Date Stamp (9) you will get the date stamp twice in the name.

The next line (10) changes depending on which output type you choose.  If you choose 'View Jasper Report'  you must select the Jasper Report template to use. These are listed on the top left of the main screen.  If you choose Create Database (3) this changes to Create Database.  If you choose Create Spreadsheet (4) it changes to Create Spreadsheet and if you choose Create .CSV File (5) it changes to Create CSV File and an extra field is displayed which allows you to choose the delimiter for the output file.  It defaults to comma (,).

Once you have defined the output you need to select a Data Selection Group (11) and a Data Filter Parameters (12) record.

Click on button (13) to save the data and exit, button (14) to cancel and discard the data.

Lastly, to actually run the report select the entry on the main screen and click on button (4).

** Note ** This screen changes slightly depending on which output type is chosen.  The screen above is for 'View Jasper Report'.  The changes are as follows:

1. Create Database : the Report Template field is disabled and shows 'Create Database'
2. Create Spreadsheet : the Report Template field is disabled and shows 'Create Spreadsheet'
3. Create CSV : the Report Template field is disabled and shows 'Create CSV File' and 2 new fields.  Field 1 allows you to select which delimiter and field 2 (Target Excel) if this is checked the output file has the text 'sep=' followed by the chosen delimiter if it is not comma(,).  This is used by Excel when opening the file.

Please refer to the following pages for more information about Report Writer:

* **[Report Writer](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer)**
* **[Report Templates](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Templates)**
* **[Data Filters](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Filters)**
* **[Data Filter Parameters](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Filter%20Parameters)**
* **[Data Selection Groups](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Selection%20Groups)**