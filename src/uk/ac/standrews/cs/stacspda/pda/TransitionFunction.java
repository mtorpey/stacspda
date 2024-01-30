package uk.ac.standrews.cs.stacspda.pda;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransitionFunction {

    /**
     * A transition from a given state.
     *
     * The machine reads an input character and a stack character, moves to a
     * state and writes a character to the stack.
     */
    private class Transition {
        String fromInput;
        String fromStack;
        State toState;
        String toStack;

        private Transition(String fromInput, String fromStack, State toState, String toStack) {
            this.fromInput = fromInput;
            this.fromStack = fromStack;
            this.toState = toState;
            this.toStack = toStack;
        }
    }

    private Map<State, List<Transition>> transitions;

    /** Constructor for an empty function, which you can add to using addTransition. */
    public TransitionFunction() {
        transitions = new HashMap<State, List<Transition>>();
    }

    /** Adds the given transition to the function. */
    public void addTransition(
                         State fromState,
                         String fromInput,
                         String fromStack,
                         State toState,
                         String toStack
                         ) {
        Transition transition = new Transition(fromInput, fromStack, toState, toStack);
        if (!transitions.containsKey(fromState)) {
            transitions.put(fromState, new ArrayList<Transition>());
        }
        transitions.get(fromState).add(transition);
    }

    /**
     * Given a particular position, returns a list of all transitions that could follow it.
     *
     * In a deterministic PDA, this will always return one position.  Since we
     * allow non-determinism, this might return none (reject) or many (branching
     * execution).
     */
    List<Position> nextPositions(Position position) {
        State fromState = position.getState();
        List<Position> nextPositions = new ArrayList<Position>();
        if (transitions.containsKey(fromState)) {
            for (Transition transition: transitions.get(fromState)) {
                if (position.readsNextInput(transition.fromInput) && position.isTopOfStack(transition.fromStack)) {
                    Position nextPosition = position.nextPosition(transition.fromInput, transition.fromStack, transition.toState, transition.toStack);
                    nextPositions.add(nextPosition);
                }
            }
        }
        return nextPositions;
    }

    /** Some validity checks to make sure this function applies to the given PDA. */
    void assertValidForPda(PushDownAutomaton pda) {
        for (State state: transitions.keySet()) {
            assert pda.isState(state);
            for (Transition t: transitions.get(state)) {
                assert pda.inInputAlphabet(t.fromInput);
                assert pda.inStackAlphabet(t.fromStack);
                assert pda.isState(t.toState);
                assert pda.inStackAlphabet(t.toStack);
            }
        }
    }

    /** Partial code to generate a diagram for the PDA, using GraphViz's DOT languge.  See PushDownAutomaton.getDotString */
    String getDotString() {
        StringBuilder builder = new StringBuilder();
        for (State fromState: transitions.keySet()) {
            Map<State, String> arrowLabels = new HashMap<>();  // arrows from this state
            for (Transition t: transitions.get(fromState)) {
                // Get info for this transition
                String fromInput = makePrintable(t.fromInput);
                String fromStack = makePrintable(t.fromStack);
                String toStack = makePrintable(t.toStack);

                // Prepare a label for this transition
                String label;
                if ((label = arrowLabels.get(t.toState)) == null) {
                    label = "";  // New arrow
                } else {
                    label += "<BR/>";  // New label on existing arrow
                }
                label += "%s,%s&rarr;%s".formatted(fromInput, fromStack, toStack);
                arrowLabels.put(t.toState, label);
            }
            // Write out the arrows from this state
            for (State toState: arrowLabels.keySet()) {
                String label = arrowLabels.get(toState);
                builder.append("  %s -> %s [label=<%s>]\n".formatted(fromState, toState, label));
            }
        }
        return builder.toString();
    }

    private String makePrintable(String letter) {
        return letter.length() == 0 ? "<I>&epsilon;</I>" : letter;
    }
    
}
