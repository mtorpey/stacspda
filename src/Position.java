/**
   Represents a configuration of a PDA at a particular moment in time, one one
   branch of execution when processing a string.

   We could call this the "state" of the PDA, but it actually includes the
   state, the position in the input, and the stack contents.

   It's immutable, and we create a new object to represent the next position,
   using the nextPosition method.
*/
public class Position {

    private PushDownAutomaton pda;
    private String inputString;
    private int inputPosition;
    private State currentState;
    private String currentStack;  // push and pop at the end

    private Position previous;

    public Position(PushDownAutomaton pda, String inputString, int inputPosition, State currentState, String currentStack) {
        this.pda = pda;
        this.inputString = inputString;
        this.inputPosition = inputPosition;
        this.currentState = currentState;
        this.currentStack = currentStack;
    }

    public Position(PushDownAutomaton pda, String inputString, int inputPosition, State currentState, String currentStack, Position previous) {
        this(pda, inputString, inputPosition, currentState, currentStack);
        this.previous = previous;
    }

    /** Starting position. */
    public Position(PushDownAutomaton pda, String inputString, State startState) {
        this(pda, inputString, 0, startState, "");
    }

    public State getState() {
        return currentState;
    }

    /** Are the next n characters in the input string equal to s? */
    public boolean readsNextInput(String s) {
        return inputString.startsWith(s, inputPosition);
    }

    /** Are the given characters equal to the top n characters on the stack (starting with the top)? */
    public boolean isTopOfStack(String s) {
        String reversed = new StringBuilder(s).reverse().toString();
        return currentStack.endsWith(reversed);
    }

    /** The position we can move to if we follow the transition specified by the arguments. */
    public Position nextPosition(String fromInput, String fromStack, State toState, String toStack) {
        // Some validity checks
        assert readsNextInput(fromInput);
        assert isTopOfStack(fromStack);

        // Calculate remaining input and stack
        int nextInputPosition = inputPosition + fromInput.length();
        String nextStack = currentStack.substring(0, currentStack.length() - fromStack.length()) + toStack;

        // Create new position after the transition is applied
        return new Position(this.pda, this.inputString, nextInputPosition, toState, nextStack, this);
    }

    /** Whether the machine has finished execution and ended in an accept state. */
    public boolean isAccepting() {
        return inputPosition == inputString.length() && pda.isAcceptState(currentState);
    }

    public String sequenceTrace() {
        if (previous != null) {
            return previous.sequenceTrace() + "\n" + toString();
        }
        return toString();
    }

    @Override
    public String toString() {
        return
            "state=" + currentState +
            " stack='" + currentStack +
            "' input='" + inputString.substring(inputPosition) +
            "'";
    }
    
}
