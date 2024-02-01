[Home](Home)>[Moneydance](Moneydance Information)>[Backgound](Background)>Accounts
# Background Information about Accounts

Moneydance has two basic data stores: Accounts and Transactions.  Transactions store activity against Accounts.

All accounts have the same information stored against them and only differ by the Account Type which can be any of:

 * ASSET 
 * BANK 
 * CREDIT_CARD 
 * EXPENSE 
 * INCOME 
 * INVESTMENT 
 * LIABILITY 
 * LOAN 
 * ROOT 
 * SECURITY

You will see a type called **ROOT**.  This is the owner of all accounts.  Accounts exist in a hierarchy, for example if you have an Investment Account with several Securities the Security entries will have the Investment Account as their *Parent* and the Investment Account will have the **ROOT** Account has its *Parent*.

You will not find the **Root** account in the list of accounts, this is only visible to the API when you are writing extensions.

**Security** accounts need to belong to an Investment account.

The same structure is used for categories.  In fact all categories are actually Accounts with a type of INCOME or EXPENSE. They too are *Children* of **ROOT**.  The category hierarchy is stored in the same way.

**Note:** If you are moving from another product to Moneydance and are using .QIF files to input your data you will get an INCOME category for each account using the Account name plus X, eg. **Mikes CurrentX**.  Transactions do not seem to be stored against these categories.  They are never used and can be deleted

[Next](Transaction Information)