[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Budget Gen)>[Introduction](https://bitbucket.org/mikerb/moneydance-2019/wiki/Introduction)>[Enter Values](https://bitbucket.org/mikerb/moneydance-2019/wiki/values)>Enter Items

# Enter Budget Items

This table has an entry for every Expense or Income Category defined within the Moneydance file.  You can delete entries and add them back in.  You can not add new entries that have not been created using the Moneydance Categories tool.

The table looks like this:

![itemslabel.jpg](https://bitbucket.org/repo/4oKeEz/images/3567521242-itemslabel.jpg)

## Fields

1. Select - click this to select the item - see [Buttons](https://bitbucket.org/mikerb/moneydance/wiki/buttons) for how it is used
2. Category - the name of the Category.  When the category is a sub-category of another it will be slightly indented.  Each category is treated separately.
3. Roll Up - if the item has child categories you can set it to be calculated from the child categories.  If you do not select this field you can enter values independent of the child categories.  If you select the item then you will not be able to enter any values.  The amount and year amounts will be calculated as a sum of the underlying child categories.
4. Amount - the amount that will be used to generate the budget amounts. **Note** this extension always deals in positive numbers.  If you are defining Income budgets do not put negative amounts here.  The amounts will be made negative when they are saved.
5. Period - the period that this expense/income is incuring.  It can be:
    * Weekly - once a week
    * Bi-weekly - every 14 days
    * Monthly - once each month
    * Ten Month - once each month but only for 10 months
    * Quarterly - once every 3 months
    * Annual - once a year
6. Start Date - the date the expenses/income starts.  The date will be changed to the period of the Budget that the Start Date falls in, for example if the Moneydance budget period is monthly this will be changed to the first of the month of the start date.
7. RPI - This percentage is added to the Budget RPI to calculate an overall RPI for this item.  It can be negative (see [RPI](https://bitbucket.org/mikerb/moneydance-2019/wiki/rpi)
8. Year 1 amount - the total of amounts that occur between the Start Date of the category and the End Date of the budget.  When the Years is 2 or 3 then there will be more columns showing the amounts for those years.

You can use previous budget or actual amounts.  The total values for the previous period are totaled and then divided by the number of actual repeats in the period.  For example if your start and end days cover one year and the period for the line (field(5)) is monthly the total for the previous period will be divided by 12.  To access these figures select the amount you are interested in (it will turn Yellow) and right click.  Two menu choices are presented:

![itemslabelpopup.jpg](https://bitbucket.org/repo/K6egeG/images/1594979671-itemslabelpopup.jpg)

Select the appropriate entry.

[Prev](https://bitbucket.org/mikerb/moneydance-2019/wiki/header)-[Next](https://bitbucket.org/mikerb/moneydance-2019/wiki/buttons)