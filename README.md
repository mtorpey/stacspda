# How to use

Download the jar file from the releases page on Github, and run as follows:

```
java -jar stacspda.jar [OPTIONS] <pda_filename> <input_string>
 -h,--help               show this help message and quit
    --show-accept-path   print all transitions on the accepting path
    --show-all           print all transitions on all branches
    --timeout <N>        give up if no accept state found after N
                         transitions
```

See the examples directory for a sample PDA file.

# Compile from source

Download

    commons-cli-1.5.0-bin.tar.gz

from

    https://commons.apache.org/proper/commons-cli/download_cli.cgi

and put

    commons-cli-1.5.0.jar

from it into the lib/ directory.

Compile with

    ./build.sh

and this should create a runnable jar file.
