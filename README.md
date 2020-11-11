# ATM_caseStudy_Wk2_CS19B046
Modified Case study done on atm

Code is Provided as .java files

The various Modules are:

1) atm- contains screen and keypad interface, and 2 other classes DepositSlot and CashDispenser.
3) Database1- This module deals with the information in database of all accounts in the bank and all transactions done in the past(2 tables are there in the .db file)
4) Transactions- This is an abstract class. This module contains other classes Withdraw, BalanceEnquiry and Deposit which provides details on the abstract method called    transaction.
5) MainClass- This is the driver class used to run the atm.
6) BankAdmin- This is a driver class from the bank end(used to manage customer details and even adding and removing of customers -ryt now it's not very restricted in terms of access).Also Adding new customers should be wrote as individual codelines (as primary key can't be repeated) and commented later on.

All of the important data is encapsulated in the respective classes and a few layers of security is added to the private data.

Constructors are extensively used in this design. It is used in a way that it allows for streamline flow of data from atm to database and vice versa(this includes the transaction class as well).

The main requirements are provided in the driver class( "MainClass" ).

The database has to be manually updated once to contain details of new customers.(Once a user is added for the first time then that data remains in the .db file for later use)

sqlite-jdbc-3.32.3.2  .jar file was used to implement the database. A .db file is also added in this repo, which contains a few transactions done in the past.

Tasks like displaying MiniStatement, Processing fee etc. has been added this time. Transfer of cash is only possible during depositing(in this design).
