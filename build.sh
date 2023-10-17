#!/bin/bash
set -e
rm -rf bin stacspda.jar
javac -d bin src/*.java
jar cfe stacspda.jar Test -C bin .
