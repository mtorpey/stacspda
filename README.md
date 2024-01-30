# How to use

Download the jar file from the releases page on Github, and run as follows:

```
java -jar stacspda.jar [OPTIONS] <pda_filename> <input_string>
```
where the possible `OPTIONS` are as follows:
```
    --diagram            render a diagram in DOT format and quit
 -h,--help               show this help message and quit
    --show-accept-path   print all transitions on the accepting path
    --show-all           print all transitions on all branches
    --timeout <N>        give up if no accept state found after N
                         transitions
```

See the examples directory for a sample PDA file.

# Diagrams

stacspda can be used to generate diagrams from PDA files.  Running the program with `--diagram` will ignore any input word and instead print out a diagram of the PDA in DOT format.  This can then be piped into the `dot` command-line utility to produce an image file.  For example:
```
java -jar stacspda --diagram example.pda | dot -Tpdf -o example.pdf
```

The `dot` utility is provided when you install graphviz.  If you don't have it, try `apt install graphviz` on a Ubuntu/Debian machine.

# Compile from source

I'd recommend using the jar file provided in a release.  But if you really want to compile this yourself, here are some instructions.

Download the Commons CLI library from

<https://archive.apache.org/dist/commons/cli/binaries/commons-cli-1.6.0-bin.tar.gz>

and put `commons-cli-1.6.0.jar` from it into the lib/ directory (which you might need to create).

Compile with

    ./build.sh

and this should create a runnable jar file.

(Newer versions of commons-cli can be found [here](https://commons.apache.org/proper/commons-cli/download_cli.cgi).)
