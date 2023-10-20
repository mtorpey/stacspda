#!/bin/bash
set -e
rm -rf bin stacspda.jar
cd lib && jar xf commons-cli-1.5.0.jar org && cd ..
javac -cp lib -d bin src/*.java src/pda/*.java
jar cfe stacspda.jar Main -C bin . -C lib .
