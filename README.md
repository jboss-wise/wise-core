# JBOSS WISE - CORE

[![Build Status](https://travis-ci.org/jboss-wise/wise-core.svg?branch=master)](https://travis-ci.org/jboss-wise/wise-core)


 Building and running the testsuite
------------------------------------

The build follows the usual Maven flow; a wilflyXYZ profile has to be specified to tell the project which target container to use for integration tests; if no wildflyXYZ profile is specified, the integration tests are skipped.

To run the full integration testsuite against the currently supported target containers use:

- mvn -Pwildfly2300 clean integration-test
- mvn -Pwildfly2201 clean integration-test
- mvn -Pwildfly2102 clean integration-test

Checkstyle
-------------------
Execution of maven-checkstyle-plugin is part of the compile phase, can be skipped by addng `-Dcheckstyle.skip=true` to the mvn command
Fastest way to get checkstyle reports is by invoking following command `mvn checkstyle:check -Dcheckstyle.failOnViolation=false`
