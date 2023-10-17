#!/bin/bash
set -e
rm -rf bin stacspda.jar
javac -d bin src/*.java src/pda/*.java
jar cfe stacspda.jar Test -C bin .
