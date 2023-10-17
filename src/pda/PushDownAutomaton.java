package pda;

import java.util.ArrayList;
import java.util.Arrays;
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

        // Do some checks
        assert states.contains(startState);
        assert states.containsAll(acceptStates);
        transitionFunction.assertValidForPda(this);

        // TODO: check alphabets don't have spaces etc.
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
    public boolean accepts(String inputString) {
        // Breadth-first search using a queue of PDA positions
        List<Position> positions = new ArrayList<>();
        List<String> branchNames = new ArrayList<>();

        // Start with the start state of this machine
        positions.add(new Position(this, inputString, startState));
        branchNames.add("");

        int nextPositionNum = 0;
        while (nextPositionNum < positions.size()) {
            // Get the next position in the queue
            Position position = positions.get(nextPositionNum);
            String branchName = branchNames.get(nextPositionNum);
            if (branchName.length() > 0) {
                System.out.print(branchName + ": ");
            }
            System.out.print(position);

            // Check for acceptance
            if (position.isAccepting()) {
                //System.out.println(position.sequenceTrace());
                System.out.println(" - accept!");
                return true;
            }

            // Get all possible next states (non-deterministic so could be 0 to many)
            List<Position> nexts = transitionFunction.nextPositions(position);
            positions.addAll(nexts);
            int numChildren = nexts.size();
            if (numChildren == 0) {
                System.out.println(" - end of branch");
            } else if (numChildren == 1) {
                branchNames.add(branchName);  // same branch name
                System.out.println();
            } else {  // numChildren > 1
                // extend branch names
                for (int i = 0; i < numChildren; i++) {
                    branchNames.add(branchName + Character.toString('A' + i));
                }
                System.out.println(" - " + numChildren + " possible branches");
            }

            // TODO: check for states already seen? (not required but would be nice)

            nextPositionNum++;
        }

        // Ran out of positions to explore, and no accepting branch found
        return false;

    }

}
