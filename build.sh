#!/bin/bash
set -e
rm -rf bin stacspda.jar
cd lib && jar xf commons-cli-1.5.0.jar org && cd ..
javac -cp lib -d bin src/uk/ac/standrews/cs/stacspda/*.java src/uk/ac/standrews/cs/stacspda/pda/*.java
jar cfe stacspda.jar uk.ac.standrews.cs.stacspda.Main -C bin . -C lib .
