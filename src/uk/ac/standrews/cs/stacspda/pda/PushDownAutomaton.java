package uk.ac.standrews.cs.stacspda.pda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PushDownAutomaton {

    private Set<State> states;
    private Set<Character> inputAlphabet;
    private Set<Character> stackAlphabet;
    private TransitionFunction transitionFunction;
    private State startState;
    private Set<State> acceptStates;

    // Usage options
    private boolean printAcceptPath;
    private boolean printAllTransitions;
    private long stepsToTimeout;

    // Checking validity
    private static final String VALID_ALPHABET_SPECIAL_CHARS = "$_.+!*'(),;/?:@=&";

    public class MaxStepsExceededException extends Exception {
        private long maxSteps;
        public MaxStepsExceededException(long maxSteps) {
            this.maxSteps = maxSteps;
        }
        public long getMaxSteps() {
            return maxSteps;
        }
    }

    public PushDownAutomaton(
                             Set<State> states,
                             Set<Character> inputAlphabet,
                             Set<Character> stackAlphabet,
                             TransitionFunction transitionFunction,
                             State startState,
                             Set<State> acceptStates
                             ) {
        // Set members
        this.states = states;
        this.inputAlphabet = inputAlphabet;
        this.stackAlphabet = stackAlphabet;
        this.transitionFunction = transitionFunction;
        this.startState = startState;
        this.acceptStates = acceptStates;

        // Usage defaults
        printAcceptPath = false;
        printAllTransitions = false;
        stepsToTimeout = -1;

        // Do some checks
        assert states.contains(startState);
        assert states.containsAll(acceptStates);
        assert isValidAlphabet(inputAlphabet);
        assert isValidAlphabet(stackAlphabet);
        transitionFunction.assertValidForPda(this);

        // TODO: check state names against some regex
    }

    public static boolean isValidAlphabet(Set<Character> alphabet) {
        return alphabet.stream().allMatch(PushDownAutomaton::isValidAlphabetChar);
    }
    
    public static boolean isValidAlphabetChar(char c) {
        // Letters, numbers and defined special characters are allowed.
        return Character.isLetterOrDigit(c) || VALID_ALPHABET_SPECIAL_CHARS.indexOf(c) != -1;
    }

    public boolean isState(State state) {
        return states.contains(state);
    }

    public boolean isAcceptState(State state) {
        return acceptStates.contains(state);
    }

    public boolean inInputAlphabet(String s) {
        return inAlphabet(s, inputAlphabet);
    }

    public boolean inStackAlphabet(String s) {
        return inAlphabet(s, stackAlphabet);
    }

    public static boolean inAlphabet(String s, Set<Character> alphabet) {
        List<Character> chars = s
            .chars()
            .mapToObj(e -> (char)e)
            .collect(Collectors.toList());
        return alphabet.containsAll(chars);
    }

    /** Does this PDA accept this string?  Might run forever. */
    public boolean accepts(String inputString) throws MaxStepsExceededException {
        // Breadth-first search using a queue of PDA positions
        List<Position> positions = new ArrayList<>();
        List<String> branchNames = new ArrayList<>();

        // Start with the start state of this machine
        positions.add(new Position(this, inputString, startState));
        branchNames.add("");

        int nextPositionNum = 0;
        while (nextPositionNum < positions.size()) {
            // Check for timeout
            if (stepsToTimeout != -1 && nextPositionNum >= stepsToTimeout) {
                throw new MaxStepsExceededException(stepsToTimeout);
            }
            
            // Get the next position in the queue
            Position position = positions.get(nextPositionNum);
            String branchName = branchNames.get(nextPositionNum);
            if (branchName.length() > 0) {
                reportTransition("Branch " + branchName + ": ");
            }
            reportTransition(position.toString());

            // Check for acceptance
            if (position.isAccepting()) {
                reportTransition(" - accept!\n");
                if (printAcceptPath) {
                    System.out.println(position.sequenceTrace());
                }
                return true;
            }

            // Get all possible next states (non-deterministic so could be 0 to many)
            List<Position> nexts = transitionFunction.nextPositions(position);
            positions.addAll(nexts);
            int numChildren = nexts.size();
            if (numChildren == 0) {
                reportTransition(" - end of branch\n");
            } else if (numChildren == 1) {
                branchNames.add(branchName);  // same branch name
                reportTransition("\n");
            } else {  // numChildren > 1
                // extend branch names
                String[] newBranchNames = new String[numChildren];
                for (int i = 0; i < numChildren; i++) {
                    newBranchNames[i] = branchName + Character.toString('A' + i);
                }
                Collections.addAll(branchNames, newBranchNames);
                reportTransition(" - splits into " + numChildren + " branches " + Arrays.toString(newBranchNames) + "\n");
            }

            // TODO: check for states already seen? (not required but would be nice)

            nextPositionNum++;
        }

        // Ran out of positions to explore, and no accepting branch found
        return false;

    }

    public void setPrintAcceptPath(boolean printAcceptPath) {
        this.printAcceptPath = printAcceptPath;
    }

    public void setPrintAllTransitions(boolean printAllTransitions) {
        this.printAllTransitions = printAllTransitions;
    }

    private void reportTransition(String s) {
        if (printAllTransitions) {
            System.out.print(s);
        }
    }

    /** Number of steps to run for before timing out. Set to -1 for no limit. */
    public void setStepsToTimeout(long maxSteps) {
        this.stepsToTimeout = maxSteps;
    }

}
