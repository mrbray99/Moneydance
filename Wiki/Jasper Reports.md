[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Report Writer](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report Writer)>Jasper Reports

Jasper Reports is an Open Source tool available from TIBCO JasperSoft which is freely available in a Community edition.  There is a host of information about this tool but it is worth starting with the [Jasper Community Wiki](https://community.jaspersoft.com/wiki/jaspersoft-community-wiki-0). 

You do not need to learn all about Jasper Reports as the Report Writer extension only uses Jasper Studio which allows the user to design reports and then fill them with data from a variety of sources.  For the Moneydance Report Writer extension it uses another Open Source tool called **h2**, which is a database, as its data source.

To use this feature of Report Writer involves installing, configuring and learning the Jaspersoft Studio tool.  There are a number of pages in this Wiki which will help you with this, though, you will have to turn to the Jasper Reports community for help on Jasper Reports.

## Installing ##

The first step is to download and install the Studio.  It is available here [JasperSoft Studio Community version](https://community.jaspersoft.com/project/jaspersoft-studio/releases).  Download the version for your computer and install it.

### Windows ###

If you wish support for 32bit Windows systems you will have to download version 6.8.0 as support for 32bit systems was withdrawn after this.

**Note:** it doesn't matter whether you use the .exe or .zip version.  The .exe version will install Jasper Studio in your program files folder.  With the .zip version you can choose where to install it.

### Apple OS/X ###

### Linux ###

Once installed you will need to configure it and customize it to be used with Report Writer.

## Configuring Jasper ##

Configuring Jasper Studio is quite simple.  Jasper Studio has a directory known as its ** workspace **. Within that directory Jasper Studio creates a directory called 'My Reports'.  It is this directory that will be used by by the Report Writer extension.

When you start Jasper Studio for the first time it will ask you to select its workspace.  Identify which directory you want and click that you wish this Studio to default to this when started.

Once Jasper has been configured you should install the Report Writer extension and identify the 'My Reports' directory as the Report Template directory.

## Using Jasper ##

To use Jasper Studio with Moneydance you need 2 files.  One is called Moneydance.xml and it contains the Data Adapter definition for the Moneydance database. It will be created in your 'My Reports' directory.  The second file is Moneydance.mv.db which is a sample database with all of the record and field definitions output by the extension.  It is created in the Output Directory you selected when configuring the extension.

Therefore before using Jasper Studio you need to install and run the Report Writer extension.