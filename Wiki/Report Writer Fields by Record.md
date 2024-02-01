[Home](https://bitbucket.org/mikerb/moneydance-2019/wiki/Home)>[Extensions](https://bitbucket.org/mikerb/moneydance-2019/wiki/Extensions)>[Report Writer](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer)>[Report Writer Filters](https://bitbucket.org/mikerb/moneydance-2019/wiki/Report%20Writer%20Filters)>Filters by Record

The filtering of records is as follows:

## Accounts ##
If an accounts start date is after the To Date it is not selected.

If specific Accounts are specified then only those accounts are output.

If specific accounts are not specified but the types of accounts is then only those accounts with the selected types are output.

If there are no filters then all accounts with a Start Date less than equal to To Date are output.

## Addresses ##

No filters apply.

## Budgets ##

If specific budgets are selected only those Budgets are output.  If none specified all Budgets are output.

## Budget Items ##

If specific budgets are selected only Budget Items for those Budgets are output. 

Budget Items are only output if any part of their range fall within the From and To Dates.

## Categories ##

If specific categories are selected only Category records for those selected Categories are output.

If specific categories are not selected but the types of categories are selected only Categories of the selected types are output.

If no categories are selected all Categories are output.

## Currencies ##

If specific currencies are selected only Currency records for those selected Currencies are output.

If specific Currencies are not selected all Currencies are output.

## Currency Rates ##
Only Currency Rate records are output if the date falls within the From and To dates.

If specific currencies are selected only Currency Rate records for those selected Currencies are output.

If specific Currencies are not selected all Currency Rate records are output.

## Investment Transactions ##

If the Transaction date is outside the From and To date range, the Transaction is not output.

If specific Categories have been selected and none of the Transaction splits contain a specified Category the Transaction is not output.

If specific Categories have not been selected but types of Category have been selected and none of the Transaction splits contain the selected types, the Transaction is not output.

If the Transaction status has been specified and the Transaction is not of a specified status the Transaction is not output.

If Tags have been selected and the Transaction does not contain any of the selected Tags the Transaction is not output.

If specific Securities have been selected, the Transaction contains a Security and it is not one of the specified Securities, the Transaction is not output.

If specific Investment Transfer Types have been specified and the Transaction is not of a specified type the Transaction is not output,

If the Transaction passes all of the above it is output.

## Reminders ##

If any part of the Reminder range falls within the From and To date range the Reminder is output.

## Securities ##

If specific Securities have been selected only those records are output.

If specific Securities have not been selected all Securities are output.

## Security Prices ##

If the date falls outside the From and To date range it is not output.

If specific Securities have been selected only those records are output.

If specific Securities have not been selected all Security Prices are output.

## Transactions ##

If the Transaction date is outside the From and To date range, the Transaction is not output.

If specific Categories have been selected and none of the Transaction splits contain a specified Category the Transaction is not output.

If specific Categories have not been selected but types of Category have been selected and none of the Transaction splits contain the selected types, the Transaction is not output.

If the Transaction status has been specified and the Transaction is not of a specified status the Transaction is not output.

If Tags have been selected and the Transaction does not contain any of the selected Tags the Transaction is not output.

If specific Accounts are specified and the Transaction does not have one of the selected Accounts the Transaction is not output.

If specific accounts are not specified but the types of Accounts is and the Transaction does not have one an Account with the selected Account Type the Transaction is not output.

If the Transaction passes all of the above it is output.