import java.util.List;

public class Main {
    public static void main(String[] args) {
        FileHandling testFile = new FileHandling(args[0]);
        Scannar scannar = new Scannar(testFile);
        scannar.scanTokens();
        List<Token> tokens = scannar.getTokens();
        Parser parser = new Parser(tokens);
        parser.parse();
    }
}