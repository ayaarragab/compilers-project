import java.util.ArrayList;
import java.util.List;

public class Scannar {

    List<Token> tokens = new ArrayList<>();
    FileHandling fileHandling = new FileHandling("test.txt");
    private boolean inMultilineComment = false;
    int totalErrors = 0;

    public List<Token> getTokens() {
        return tokens;
    }

    String[] keywords = {
        "true", "false", "and", "or"
    };

    private boolean isIdentifier(String word) {
        if (word.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            for (String keyword : keywords) {
                if (word.equals(keyword)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isConstant(String word) {
        if (word.matches("[0-9]+")) // Numbers
            return true;
        if (word.matches("'[A-Za-z]'")) // Single characters
            return true;
        if (word.matches("\".*\"")) // String literals
            return true;
        for (String keyword : keywords) {
            if (word.equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isKeyword(String word) {
        for (String keyword : keywords) {
            if (word.equals(keyword)) {
                return true; // Recognize 'and' and 'or' as keywords
            }
        }
        return Keyword.isKeyword(word);
    }

    private void saveToken(String text, String dataType, int lineNumber, int state) {
        tokens.add(new Token(text, dataType, lineNumber, state));
    }

    public void scanTokens() {
        fileHandling.openFile();

        for (int lineNumber = 1; lineNumber <= fileHandling.countLines(); lineNumber++) {
            String line = fileHandling.readFile();
            if (line == null)
                continue;

            line = preprocessLine(line);
            if (line == null || line.trim().isEmpty())
                continue;

            processLine(line, lineNumber);
        }

        printTokens();

        fileHandling.closeFile();
    }

    private void printTokens() {
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.printf("Total NO of errors: %d\n", totalErrors);
    }

    private void processLine(String line, int lineNumber) {
        List<String> tokenized = tokenizeLine(line);

        for (String value : tokenized) {
            if (isKeyword(value)) {
                saveToken(value, Keyword.getTokenType(value), lineNumber, 1);
            } else if (isIdentifier(value)) {
                saveToken(value, "Identifier", lineNumber, 1);
            } else if (isConstant(value)) {
                saveToken(value, "Constant", lineNumber, 1);
            } else {
                saveToken(value, "Unknown", lineNumber, 0);
                totalErrors++;
            }
        }
    }

    private String preprocessLine(String line) {
        line = line.trim();
        if (line.isEmpty())
            return null;

        if (inMultilineComment) {
            if (line.contains("##/")) {
                inMultilineComment = false;
                return line.substring(line.indexOf("##/") + 3);
            } else {
                return null;
            }
        }

        if (line.contains("/##")) {
            int start = line.indexOf("/##");
            int end = line.indexOf("##/", start + 3);

            if (end != -1) {
                line = line.substring(0, start) + line.substring(end + 3);
            } else {
                inMultilineComment = true;
                line = line.substring(0, start);
            }
        }

        if (line.contains("/-")) {
            line = line.substring(0, line.indexOf("/-"));
        }

        return line.trim();
    }

    private List<String> tokenizeLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
    
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
    
            if (ch == '"' && !inString) {
                inString = true;
                current.append(ch);
            } else if (ch == '"' && inString) {
                inString = false;
                current.append(ch);
                tokens.add(current.toString());
                current.setLength(0);
            } else if (inString) {
                current.append(ch);
            } else if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '–' || ch == '/') {
                current.append(ch);
            } else {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
    
                if (!Character.isWhitespace(ch)) {
                    // Check for multi-character operators like ==, >=, etc.
                    if ((ch == '=' || ch == '!' || ch == '<' || ch == '>') && i + 1 < line.length()
                            && line.charAt(i + 1) == '=') {
                        tokens.add("" + ch + "=");
                        i++;
                    } else if ((ch == '&' || ch == '|') && i + 1 < line.length() && line.charAt(i + 1) == ch) {
                        tokens.add("" + ch + ch);
                        i++;
                    } else {
                        tokens.add(Character.toString(ch));
                    }
                }
            }
        }
    
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
    
        return tokens;
    }
}