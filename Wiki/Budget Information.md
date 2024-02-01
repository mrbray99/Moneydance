[Home](Home)>[Moneydance](Moneydance Information)>[Backgound](Background)>Budgets
# Background Information about Budgets

There has been quite a lot of talk in the forums about **Budgets**.  Mainly because the budgeting capabilities of Moneydance are rudimentary at best.  Which has led to a plethora of extensions, mine ([see Extension](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)) included.  I believe that there is also a misunderstanding on how budgets work.

In Moneydance Budgets only work on Categories.  Basically you can enter how much expenditure/income you expect on a particular category and then report on actual verses budgeted.  These reports use Transaction type 2 and 3 (see [Transaction Information](Transaction Information)) for that reporting.

Moneydance allows you to create **Budget Items**.  Each **Budget Item** identifes the following:

* Category being budgeted
* Date range it applies to
* Amount

The date range depends on the type of budget set up.  If it is of the *New Style* then the range will match the period chosen when the budget was created in the Budget Manager. When you update the budget amount on the budget screen, Moneydance will change the Amount on the corresponding Budget Item.

Budgets of the *Mixed Style* are stored differently and I have not investigated how they work.  All I can say is that they too use the Categories.

Though you can Budget income and expenses it is only against the Categories.  You can not budget against Accounts.  As mentioned in [Accounts Information](Accounts Information) there maybe an **Income** category for each account, however, no transactions are stored against them.  If you budget against these catagories there will not be any Actuals when you report.

Budgeting is really a prediction of income/expense split by category.  Budget Reporting is a comparison of actuals (Transactions type 2 and 3) against Budget predictions by category.

Budgeting is not a running budget of your accounts, ie. net worth.  You can not determin predicted cash flow from a budget.

[Prev](Transaction Information) [Next](Loans and Reminders)