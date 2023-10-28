import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pda.PushDownAutomaton;

public class Main {

    private static final String USAGE =
        "java -jar stacspda.jar [OPTIONS] <pda_filename> <input_string>";

    public static void main(String[] args) {
        Options options = createCommandLineOptions();
        CommandLineParser parser = new DefaultParser();

        try {
            // Process arguments
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                printUsage(options);
                return;
            }
            args = cmd.getArgs();
            String filename = args[0];
            String input = args[1];

            // Read PDA from file
            PdaReader reader = new PdaReader(filename);
            PushDownAutomaton pda = reader.readPda();

            // Apply user options
            pda.setPrintAllTransitions(cmd.hasOption("show-all"));
            pda.setPrintAcceptPath(cmd.hasOption("show-accept-path"));
            if (cmd.hasOption("timeout")) {
                long steps = Long.parseLong(cmd.getOptionValue("timeout"));
                pda.setStepsToTimeout(steps);
            }

            // Run PDA on input string
            System.out.println(pda.accepts(input));
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printUsage(options);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Not enough arguments.");
            printUsage(options);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (PdaReader.InvalidPdaFormatException e) {
            System.err.println("Invalid format: " + e.getDescription());
        } catch (PushDownAutomaton.MaxStepsExceededException e) {
            System.err.println("Gave up after " + e.getMaxSteps() + " steps without accepting");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, options);
    }

    private static Options createCommandLineOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "show this help message and quit");
        options.addOption(longOption("show-accept-path", "print all transitions on the accepting path", null));
        options.addOption(longOption("show-all", "print all transitions on all branches", null));
        options.addOption(longOption("timeout", "give up if no accept state found after N transitions", "N"));
        return options;
    }

    /** Create an option with a long name but no short name. */
    private static Option longOption(String name, String desc, String argName) {
        Option.Builder builder = Option.builder().longOpt(name).desc(desc);
        if (argName != null) {
            builder.hasArg().argName(argName);
        }
        return builder.build();
    }

}
