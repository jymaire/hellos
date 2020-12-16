# Releases notes

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