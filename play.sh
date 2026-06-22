#!/bin/bash
clear
mvn compile -q
mvn exec:java -Dexec.mainClass="com.tictactoe.App" -q
