# Road map

List of features to implement (without external dependencies)
* ~~add a button to credit Cyclos account from the list of Hello Asso payments ( update status of payment (to do/success/error))~~ 
* when previous task is tested in production, automate it
* prevent payment twice by checking status before credit account
* log in an external file all the payments from Hello Asso 
* log in an external file all the payments to Cyclos
* add transaction management
* paginate result
* clean up pom.xml (exclude unused dependencies to reduce jar sized) + add fixed versions as parameters
* log in file last payment received at shut down of the application
* push update (no need to refresh page to see new payments/update of status)
* handle correctly Hello Asso token (get first access token at start up, then request refresh token and finally ask one new refresh token each night)
* check there is no dependency with "Gonette" project in source code (all should be in .env file)
* fix render of date format (make it more human friendly)
* add doc in French (hello asso api key generation, Cyclos user configuration, .env file to create)
* add full demo mode to be able to run the application without production credentials
* add credit for freepik
* add limit amount (250€) per payment with mail notif -> test todo
* add check from cyclos to prevent double payment
* remove token from DB and store it in a static map