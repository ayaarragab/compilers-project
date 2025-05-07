public class Token {
    private String text;
    private String type;
    private int lineNumber;
    private int state = 0;

    public Token(String text, String type, int lineNumber, int state) {
        this.text = text;
        this.type = type;
        this.lineNumber = lineNumber;
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getState() {
        return state;
    }


    @Override
    public String toString() {
        if (state == 0){
            return  String.format("Error token: Line #: %d  Token Text: %s ",
                    this.lineNumber, this.text, this.type );
        }
        else {

            return String.format("Correct token: Line #: %d  Token Text: %s  Token Type: %s",
                    this.lineNumber, this.text, this.type);
        }
    }

}
