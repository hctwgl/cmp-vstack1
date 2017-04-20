#!/bin/bash
cd $1
#/usr/local/apache-maven-3.3.9/bin/mvn -DskipTests clean install
mvn -DskipTests clean install
