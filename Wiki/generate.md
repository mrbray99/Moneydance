[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Budget Gen)>[Introduction](https://bitbucket.org/mikerb/moneydance-2019/wiki/Introduction)>Generate

# Generating your Budget Entries

## Introduction

This is the main results window.  Once you have calculated your budget values you need to translate them into actual Budget Items that match the Moneydance budget you are working with.

This window will contain an entry for each category in the values window that has an amount or has Roll Up selected.  The window looks like this:

![generatelabel.jpg](https://bitbucket.org/repo/K6egeG/images/1292627201-generatelabel.jpg)

## Roll Up

If you have chosen to roll up some items into a parent item then both the parent and child items are shown.  You need to decide which one to put into the Budget.  If you choose both then your budget will be doubled up as shown in the example above. Roll Up Test, Roll Up Parent and the two Roll Up Child rows have been saved into the Budget

## Fields

Each  Category has 2 lines (fields 1,2,3 and fields 4).  The first line is the set of generated values for the date periods displayed.  Fields 4 contain the current values, i.e. the values currently in the Moneydance Budget for the displayed date periods.

Field 1 - select this if you wish to update the values for this category
Field 2 - is the name of the category
Field 3 - are the amounts generated

If you wish to manage the values for years 2 or 3 change the Years field (5) using the drop down.  The date periods and values will change.

## Operation

If you have Generated the figures you can move on to updating the Moneydance Budget.  Alternatively you can alter the amounts.  You can select each figure (field (3) and enter a new value.  If you right click on an amount you have three options (10):

* Use budget amount from previous period
* Use actual amount from previous period
* Copy this amount across the line to the end.

If yu have changed the values and wish to save them to the parameter file, click on (6).  You will be asked for a file name as on the Enter Values screen.

To update the values in the Moneydance file select the categories you are interested in Field (1) and then click on 'Create Budget Items' button (7).  This will place the generated values into the file and you will see the 'Current Items' change.

If the Moneydance file has existing values but no value has been generated for those date periods these are left as is unless you select 'Delete Current Value' field 8.  If you select this, values where the generated value is zero will be deleted.

Use button (9) when you have finished.

Each year has to be processed separately.

**Note** Income amounts are shown positive but will be stored as negative amounts as per the Moneydance Budget interface.


[Introduction](https://bitbucket.org/mikerb/moneydance-2019/wiki/Introduction)-[Prev](https://bitbucket.org/mikerb/moneydance-2019/wiki/rpi)
