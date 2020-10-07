# README #

### What is this repository for? ###

* This repository contains search and recomendations api related api tests.

### How do I get set up? ###

* First install and configure java, configure home path for java.
* Clone repository in local system and do maven clean install
* Java 11, Rest-Assured Api's
* Database configuration -> No
* Deployment instructions

* Local Run Tests => Add local-global.properties with own local config.
* command for single Test => 
    mvn clean install -Denv=local -Dtype=reco -Ddevice_type=android -Dtest=RecomendedTracks.java
* command for suite Test =>
    mvn clean install -Denv=local -Dtype=reco -Ddevice_type=android -DsuiteXmlFile=testng.xml

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Execution Queries
* Single Test : mvn clean install -Denv=local -Dtype=reco -Ddevice_type=android -Dtest=RecomendedTracks.java
* Test Suites : mvn clean install -Denv=local -Dtype=reco -Ddevice_type=android -DsuiteXmlFile=testng.xml