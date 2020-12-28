# Hellos
(say "Helios")

The aim of this project is to listen to a Hello Asso account to add credit on a Cyclos account.
The business context is local currencies.

## Technical pre requisites
- Java 11
- Maven 3

## Packaging guide

To deploy this app, you need to run the Maven package command `mvn -DskipTests=true package` in the directory where the pom.xml file is (root directory of the project).
Then, you have a jar file named `hellos-0.0.1-SNAPSHOT.jar` (version may change) in the `target` directory.
This jar is an executable file, so you can execute it. To register it as service, you follow the Spring Boot documentation https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment-install .

You can also create a Docker image by running `mvn spring-boot:build-image` command.

## Dev guide

To have some data at start up, you can activate `dev` Spring profile by adding `-Dspring.profiles.active=dev` to the VM options.

## Resources
A French documentation is available in `doc/fr` folder.
Other projects using Hello Asso and Cyclos APIs :
- Cairn (in PHP)  
https://github.com/cairn-monnaie/cel
- Florain (in Python)   
https://github.com/e-Florain/cyclos-python
 