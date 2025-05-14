public class Main {
    public static void main(String[] args) {
        FileHandling testFile = new FileHandling(args[0]);
        Scannar scannar = new Scannar(testFile);
        scannar.scanTokens();
        
        Parser parser = new Parser(scannar.getTokens());
        parser.parse();
    }
}