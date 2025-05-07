import java.util.HashMap;

public class Keyword {
    public static final HashMap<String, String> keywords = new HashMap<>();

    static {

        keywords.put("Division", "Class");
        keywords.put("InferedFrom", "Inheritance");
        keywords.put("WhetherDo–Else", "Condition");
        keywords.put("Ire", "Integer");
        keywords.put("Sire", "SInteger");
        keywords.put("Clo", "Character");
        keywords.put("SetOfClo", "String");
        keywords.put("FBU", "Float");
        keywords.put("SFBU", "SFloat");
        keywords.put("None", "Void");
        keywords.put("Logical", "Boolean");
        keywords.put("terminatethis", "Break");
        keywords.put("Rotatewhen", "Loop");
        keywords.put("Continuewhen", "Loop");
        keywords.put("Replywith", "Return");
        keywords.put("Seop", "Struct");
        keywords.put("Check–situationof", "Switch");
        keywords.put("Program", "Stat");
        keywords.put("End", "End");
        keywords.put("Using", "Inclusion");


        keywords.put("+", "Arithmetic Operation");
        keywords.put("-", "Arithmetic Operation");
        keywords.put("*", "Arithmetic Operation");
        keywords.put("/", "Arithmetic Operation");
        keywords.put("==", "relational operators");
        keywords.put("<", "relational operators");
        keywords.put(">", "relational operators");
        keywords.put("!=", "relational operators");
        keywords.put("<=", "relational operators");
        keywords.put(">=", "relational operators");
        keywords.put("&&", "Logic operators");
        keywords.put("||", "Logic operators");
        keywords.put("~", "Logic operators");
        keywords.put("=", "Assignment operator");
        keywords.put(":", "Assignment operator");
        keywords.put(".", " Dot ACCESS");
        keywords.put("Function", "function");

        keywords.put("{", "Braces");
        keywords.put("}", "Braces");
        keywords.put("[", "Braces");
        keywords.put("]", "Braces");
        keywords.put("(", "Braces");
        keywords.put(")", "Braces");
        keywords.put(";", "SEMICOLON");
        keywords.put(",", "COMMA");

        keywords.put("\"", "DQUOTE");
        keywords.put("'", "SQUOTE");
    }

    public static boolean isKeyword(String word) {
        return keywords.containsKey(word);
    }

    public static String getTokenType(String word) {
        return keywords.getOrDefault(word, "IDENTIFIER");
    }
}


