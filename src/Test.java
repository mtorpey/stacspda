import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        testPda_2_17();
    }

    private static void testPda_2_17() {
        // Get PDA
        String filename = "../examples/pda_2_17.pda";
        PdaReader reader = new PdaReader(filename);
        try {
            PushDownAutomaton pda = reader.readPda();

            // Test true
            System.out.println(pda.accepts(""));
            System.out.println(pda.accepts("aaabccc"));
            System.out.println(pda.accepts("ac"));
            System.out.println(pda.accepts("ab"));
            System.out.println(pda.accepts("abbbc"));
            System.out.println(pda.accepts("bbbbb"));

            // Test false
            System.out.println(pda.accepts("abbccc"));
            System.out.println(pda.accepts("bc"));
            System.out.println(pda.accepts("aaabbc"));
            System.out.println(pda.accepts("a"));
            System.out.println(pda.accepts("aaaaaaccccc"));
            System.out.println(pda.accepts("aaacbbb"));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        } catch (PdaReader.InvalidPdaFormatException e) {
            System.err.println("Invalid format: " + e.getDescription());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void test_pda2_15() {
        State q1 = new State("q1");
        State q2 = new State("q2");
        State q3 = new State("q3");
        State q4 = new State("q4");
        Set<State> states = new HashSet<>(Arrays.asList(q1, q2, q3, q4));
        
        Set<Character> inputAlphabet = new HashSet<>(Arrays.asList('0', '1'));
        Set<Character> stackAlphabet = new HashSet<>(Arrays.asList('0', '$'));

        TransitionFunction tf = new TransitionFunction();
        tf.addTransition(q1, "", "", q2, "$");
        tf.addTransition(q2, "0", "", q2, "0");
        tf.addTransition(q2, "1", "0", q3, "");
        tf.addTransition(q3, "1", "0", q3, "");
        tf.addTransition(q3, "", "$", q4, "");

        State start = q1;
        Set<State> acceptStates = new HashSet<>(Arrays.asList(q1, q4));

        PushDownAutomaton pda2_15 = new PushDownAutomaton(states, inputAlphabet, stackAlphabet, tf, start, acceptStates);

        System.out.println(pda2_15.accepts(""));
        System.out.println(pda2_15.accepts("01"));
        System.out.println(pda2_15.accepts("0011"));
        System.out.println(pda2_15.accepts("0000011111"));
        System.out.println(pda2_15.accepts("11"));
        System.out.println(pda2_15.accepts("00111"));
        System.out.println(pda2_15.accepts("0"));
        System.out.println(pda2_15.accepts("1100"));
    }

}
