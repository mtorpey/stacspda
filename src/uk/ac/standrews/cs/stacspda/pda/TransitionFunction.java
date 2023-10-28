package uk.ac.standrews.cs.stacspda.pda;

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
                    // TODO: something to handle branch names?
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
    
}
