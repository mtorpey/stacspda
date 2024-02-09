package uk.ac.standrews.cs.stacspda.pda;

/**
   A state in the set of states belonging to a PDA.

   Only knows its name.  States with the same name are equal.
*/
public class State {

    private String name;

    public State(String name) {
        assert isValidName(name);
        this.name = name;
    }

    public static boolean isValidName(String name) {
        return name.matches("[_A-Za-z][_\\$0-9A-Za-z]*");
    }

    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return this.toString().equals(((State) obj).toString());
    }

    public String getDotString() {
        // Quote if contains dollar
        return name.contains("$") ? "\"" + name + "\"" : name;
    }
}
