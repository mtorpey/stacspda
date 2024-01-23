package uk.ac.standrews.cs.stacspda;

import uk.ac.standrews.cs.stacspda.pda.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class PdaReader {

    private static final String SEPARATOR = ">";
    private String filename;

    public PdaReader(String filename) {
        this.filename = filename;
    }

    public PushDownAutomaton readPda() throws IOException, InvalidPdaFormatException {
        try(Scanner lineScanner = new Scanner(new File(filename));) {
            // Get strings from header lines in file
            List<String> stateNames = tokensFromNextHeaderLine(lineScanner, "States");
            String startStateName = singleTokenFromNextHeaderLine(lineScanner, "StartState");
            List<String> acceptStateNames = tokensFromNextHeaderLine(lineScanner, "AcceptStates");
            String inputAlphString = singleTokenFromNextHeaderLine(lineScanner, "InputAlphabet");
            String stackAlphString = singleTokenFromNextHeaderLine(lineScanner, "StackAlphabet");

            // Validate state names
            checkStateNames(stateNames);
            checkStartStateName(stateNames, startStateName);

            // Process the strings into args for constructor
            Set<State> states = statesFromStrings(stateNames);
            State startState = new State(startStateName);
            Set<State> acceptStates = statesFromStrings(acceptStateNames);
            Set<Character> inputAlphabet = alphabetFromString(inputAlphString);
            Set<Character> stackAlphabet = alphabetFromString(stackAlphString);

            // Check alphabets
            checkAlphabet(inputAlphabet);
            checkAlphabet(stackAlphabet);

            // Assemble transition function, checking along the way
            TransitionFunction tf = new TransitionFunction();
            String line;
            while ((line = nextPdaLine(lineScanner)) != null) {
                try(Scanner s = new Scanner(line);) {
                    // 1. from state
                    State fromState = new State(s.next());
                    if (!stateNames.contains(fromState.toString())) {
                        throw new InvalidPdaFormatException("State '" + fromState + "' not in list of states");
                    }

                    // 2. string read from input
                    String input = processString(s.next());
                    if (!PushDownAutomaton.inAlphabet(input, inputAlphabet)) {
                        throw new InvalidPdaFormatException("Input '" + input + "' contains characters not in input alphabet");
                    }
                    checkSingleChar(input, "Input letter");

                    // 3. string to pop from stack
                    String fromStack = processString(s.next());
                    if (!PushDownAutomaton.inAlphabet(fromStack, stackAlphabet)) {
                        throw new InvalidPdaFormatException("Popped string '" + fromStack + "' contains characters not in stack alphabet");
                    }
                    checkSingleChar(fromStack, "Popped letter");

                    // 4. separator for readability
                    if (!s.next().equals(SEPARATOR)) {
                        throw new InvalidPdaFormatException("Expected " + SEPARATOR + " as 4th symbol on line '" + line + "'");
                    }

                    // 5. string to push onto stack
                    String toStack = processString(s.next());
                    if (!PushDownAutomaton.inAlphabet(toStack, stackAlphabet)) {
                        throw new InvalidPdaFormatException("Pushed string '" + toStack + "' contains characters not in stack alphabet");
                    }
                    checkSingleChar(toStack, "Pushed letter");

                    // 6. state to move to
                    State toState = new State(s.next());
                    if (!stateNames.contains(toState.toString())) {
                        throw new InvalidPdaFormatException("State '" + toState + "' not in list of states");
                    }

                    // end of line
                    if (s.hasNext()) {
                        throw new InvalidPdaFormatException("Too many symbols on line '" + line + "'");
                    }

                    // if all went well, add this transition
                    tf.addTransition(fromState, input, fromStack, toState, toStack);
                }
            }
            return new PushDownAutomaton(states, inputAlphabet, stackAlphabet, tf, startState, acceptStates);
        }  // pass on any exceptions
    }

    private void checkStateNames(List<String> stateNames) throws InvalidPdaFormatException {
        for (String stateName: stateNames) {
            if (!State.isValidName(stateName)) {
                throw new InvalidPdaFormatException("Invalid state name '" + stateName + "'");
            }
        }
    }

    private void checkStartStateName(List<String> stateNames, String startStateName) throws InvalidPdaFormatException {
        if (!stateNames.contains(startStateName)) {
            throw new InvalidPdaFormatException("Start state '" + startStateName + "' not in list of states");
        }
    }

    private void checkAlphabet(Set<Character> alphabet) throws InvalidPdaFormatException {
        for (char c: alphabet) {
            if (!PushDownAutomaton.isValidAlphabetChar(c)) {
                throw new InvalidPdaFormatException("Invalid alphabet character '" + c + "'");
            }
        }
    }

    /* Our PDAs could read, push and pop multiple characters at once, but the Sipser definition allows max one character per transition. */
    private void checkSingleChar(String input, String description) throws InvalidPdaFormatException {
        if (input.length() > 1) {
            throw new InvalidPdaFormatException(description + " must be a single character, not '" + input + "'");
        }
    }

    private String singleTokenFromNextHeaderLine(Scanner lineScanner, String expectedTitle) throws InvalidPdaFormatException {
        List<String> tokens = tokensFromNextHeaderLine(lineScanner, expectedTitle);
        if (tokens.size() != 1) {
            throw new InvalidPdaFormatException("Expected one token for " + expectedTitle + " but found " + tokens.size());
        }
        return tokens.get(0);
    }

    private List<String> tokensFromNextHeaderLine(Scanner lineScanner, String expectedTitle) throws InvalidPdaFormatException {
        Scanner s = new Scanner(nextPdaLine(lineScanner));

        // Check first symbol
        String propertyName = s.next();
        if (!propertyName.equals(expectedTitle + ":")) {
            throw new InvalidPdaFormatException("Expected " + expectedTitle + " next, but found " + propertyName);
        }

        // Get values
        List<String> tokens = new ArrayList<>();
        while (s.hasNext()) {
            tokens.add(s.next());
        }
        return tokens;
    }

    private static Set<State> statesFromStrings(Collection<String> names) {
        return names.stream().map(name -> new State(name)).collect(Collectors.toSet());
    }

    private Set<Character> alphabetFromString(String s) {
        return s.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
    }

    private static String nextPdaLine(Scanner scanner) {
        // Search for a non-trivial line
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // remove comments
            int commentStart = line.indexOf('#');
            if (commentStart != -1) {
                line = line.substring(0, commentStart);
            }

            // cut leading/trailing whitespace
            line = line.strip();

            // ignore empty lines
            if (!line.equals("")) {
                return line;
            }
        }

        // No lines left
        return null;
    }

    // If string is "-", this is the empty string
    private static String processString(String s) {
        if (s.equals("-")) {
            return "";
        }
        return s;
    }

    public static class InvalidPdaFormatException extends Exception {

        private String description;

        public InvalidPdaFormatException(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

}
