public class Main {
    public static void main(String[] args) {
        Scannar scannar = new Scannar();
        scannar.scanTokens();
        
        Parser parser = new Parser(scannar.getTokens());
        parser.parse();
    }
}