# Releases notes

## 0.0.5

- add possibility to have an email different between Hello Asso and Cyclos accounts (optional email in Hello Asso form)
- add new status for automatic payments
- fix : method "handleNewPayment" in "PaymentService" used to read email from configuration file instead of database

## 0.0.4

* add administration panel to (des)activate automatic payments and payments into cyclos
* add possibility to change email recipient also through panel admin
* fix lot of small typo and bug possible situation (static code analysis)
* add more details on error page

## 0.0.3
* fix bug when error message is too long

## 0.0.2
* fix bug when amount was too high (payment was saved in cents instead of unit)
* prevent one payment to be executed twice
* add an error page
* add Enum to represent the state of a Hello Asso payment
* add some unit tests

## 0.0.1
* create endpoint to receive Hello Asso payments
* create in memory database to store payment
* manuel credit into Cyclos
* scheduled purge of database
* user interface secured by login