import java.io.FileNotFoundException;
import java.io.IOException;

import pda.PushDownAutomaton;

public class Main {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Usage: java -jar stacspda.jar <pda_filename> <input_string>");
        }
        String filename = args[0];
        String input = args[1];

        PdaReader reader = new PdaReader(filename);
        try {
            PushDownAutomaton pda = reader.readPda();
            System.out.println(pda.accepts(input));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        } catch (PdaReader.InvalidPdaFormatException e) {
            System.err.println("Invalid format: " + e.getDescription());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
