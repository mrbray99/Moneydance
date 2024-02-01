[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Budget Gen)>Introduction

# Introduction

The idea behind Budget Generator is that when budgeting people usually determine what type of costs and income they have, how much they will spend/receive and how often.  The Generator allows you to enter your data in this way.  Once you are happy with the overall budget you generate the budget entries required by Moneydance.

This does not replace the Moneydance budget process but compliments it.  Once generated you can use Moneydance to adjust and report on your budget.

In addition you do not have to generate the whole budget you can generate entries for an individual category.

To use the Budget Generator you:

* [Select/Create a Budget](https://bitbucket.org/mikerb/moneydance-2019/wiki/budget)
* [Enter budget values for expense and income categories](https://bitbucket.org/mikerb/moneydance-2019/wiki/values)
* [Add new  categories](https://bitbucket.org/mikerb/moneydance-2019/wiki/add)
* [Generate and save new budget entries](https://bitbucket.org/mikerb/moneydance-2019/wiki/generate)
* [Calculate budgets for up to three years with appropriate increase due to Retail Price Index](https://bitbucket.org/mikerb/moneydance-2019/wiki/rpi)

The Generator saves the parameters in a file in the folder of the current Moneydance file with a name of the Internal Key for the budget plus an extension of .BPIC for income categories and .BPEX for expense categories.  These files are created when you open the Generator for the first time for the particular budget and reloaded each time you open the Generator.

## Types of Budgets/Categories Supported
Budget Generator only works with Budgets that have been created in Moneydance using the Budget Manager and do not have a period type of **Mixed**.  Budgets with a period type of **Mixed** are 'old' style budgets.

## Types of Budget Pattern

Budget Generator allows you to enter a different 'Pattern' for each Category.  Each 'Pattern' has the following:

1. An amount - this is the amount of the actual expense/income
2. A 'period' - this is the length of time between each payment/receipt. This does not have to match the period type on the budget.
3. A start date - the date of the first payment/receipt
4. A Retail Price Index entry - see [RPI](https://bitbucket.org/mikerb/moneydance-2019/wiki/rpi)

The Generator will generate budget entries for each pattern matching the entries with the Period Type of the chosen budget.

# Start

First select the budget [select](https://bitbucket.org/mikerb/moneydance-2019/wiki/budget)