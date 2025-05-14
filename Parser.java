import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;
    private int errorCount;
    private List<String> parseResults;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.errorCount = 0;
        this.parseResults = new ArrayList<>();
        
        if (!tokens.isEmpty()) {
            this.currentToken = tokens.get(0);
        }
    }

    // Main parsing method to start the process
    public void parse() {
        program();
        printParseResults();
    }

    // Print parsing results
    private void printParseResults() {
        for (String result : parseResults) {
            System.out.println(result);
        }
        System.out.println("Total NO of errors: " + errorCount);
    }

    // Add a match result to the parse results
    private void addMatchResult(String rule) {
        parseResults.add("Line : " + currentToken.getLineNumber() + " Matched Rule used: " + rule);
    }

    // Add an error result to the parse results
    private void addErrorResult(String errorMessage) {
        parseResults.add("Line : " + currentToken.getLineNumber() + " Not Matched Error: " + errorMessage);
        errorCount++;
    }

    // Advance to the next token
    private void advance() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        }
    }

    // Check if the current token matches the expected type
    private boolean match(String type) {
        if (currentToken.getType().equals(type)) {
            advance();
            return true;
        }
        return false;
    }

    // Check if the current token matches the expected text
    private boolean matchText(String text) {
        if (currentToken.getText().equals(text)) {
            advance();
            return true;
        }
        return false;
    }

    // Main grammar rules implementation
    // 1. Program → ClassDeclarationList End.
    private void program() {
        if (matchText("Program")) {
            addMatchResult("Program");
            boolean isClass = classDeclarationList();
            if (isClass) {
                if (matchText("End")) {
                    addMatchResult("End");
                } else {
                    addErrorResult("Expected 'End' statement");
                }
            } else {
                
            }
        } else {
            addErrorResult("Expected 'Program' statement");
        }
    }
    // 2. ClassDeclarationList → ClassDeclaration ClassDeclarationList | ε
    private boolean classDeclarationList() {
        while (currentTokenIndex < tokens.size() && currentToken.getText().equals("Division")) {
            classDeclaration();
        }
        return false;
        // ε case - do nothing if no more Division tokens
    }
    // 3. ClassDeclaration → Division ID { ClassImplementation }
    //                        | Division ID InferredFrom { ClassImplementation }
    private void classDeclaration() {
        if (matchText("Division")) {
            addMatchResult("ClassDeclaration");
            if (currentToken.getType().equals("Identifier")) {
                advance(); // Consume ID
                
                // Check for inheritance
                if (matchText("InferedFrom")) {
                    if (currentToken.getType().equals("Identifier")) {
                        advance(); // Consume inherited class ID
                    } else {
                        addErrorResult("Expected identifier after 'InferedFrom'");
                    }
                }
                
                if (matchText("{")) {
                    classImplementation();
                    if (matchText("}")) {
                        // Class declaration completed successfully
                    } else {
                        addErrorResult("Expected '}'");
                    }
                } else {
                    addErrorResult("Expected '{'");
                }
            } else {
                addErrorResult("Expected identifier after 'Division'");
            }
        } else {
            addErrorResult("Expected 'Division'");
        }
    }

    // 4. ClassImplementation → ClassItem ClassImplementation | ε
    private void classImplementation() {
        if (currentTokenIndex < tokens.size() && 
            !currentToken.getText().equals("}")) {
            classItem();
            classImplementation();
        }
        // ε case - do nothing
    }

    // 5. ClassItem → VarDeclaration | MethodDeclaration | Comment | UsingCommand | FuncCall
    private void classItem() {
        if (isType(currentToken.getText())) {
            // Could be VarDeclaration or MethodDeclaration
            int savedTokenIndex = currentTokenIndex;
            Token savedToken = currentToken;
            advance(); // Consume type
            if (currentToken.getType().equals("Identifier")) {
                advance(); // Consume ID
                if (currentToken.getText().equals("(")) {
                    // It's a MethodDeclaration
                    currentTokenIndex = savedTokenIndex;
                    currentToken = savedToken;
                    methodDeclaration();
                } else {
                    // It's a VarDeclaration
                    currentTokenIndex = savedTokenIndex;
                    currentToken = savedToken;
                    varDeclaration();
                }
            } else {
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                addErrorResult("Expected identifier after type");
            }
        } else if (currentToken.getText().equals("/##") || currentToken.getText().equals("/-")) {
            comment();
        } else if (currentToken.getText().equals("using")) {
            usingCommand();
        } else if (currentToken.getType().equals("Identifier")) {
            // Could be a FuncCall
            int savedTokenIndex = currentTokenIndex;
            Token savedToken = currentToken;
            advance(); // Consume ID
            if (currentToken.getText().equals("(")) {
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                funcCall();
            } else {
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                addErrorResult("Invalid class item");
                advance(); // Skip to avoid infinite loop
            }
        } else {
            addErrorResult("Invalid class item");
            advance(); // Skip to avoid infinite loop
        }
    }
    // 6. MethodDeclaration → FuncDecl ; | FuncDecl { VarDeclaration Statements }
    private void methodDeclaration() {
        int savedTokenIndex = currentTokenIndex;
        Token savedToken = currentToken;
    
        if (funcDecl()) {
            if (matchText(";")) {
                addMatchResult("MethodDeclaration");
            } else if (matchText("{")) {
                varDeclaration(); // Optional variable declarations
                statements(); // Statements inside the block
                if (matchText("}")) {
                    addMatchResult("MethodDeclaration");
                } else {
                    addErrorResult("Expected '}' after method body");
                }
            } else {
                addErrorResult("Expected ';' or '{' after function declaration");
            }
        } else {
            // Reset token index if funcDecl fails to avoid consuming incorrect tokens
            currentTokenIndex = savedTokenIndex;
            currentToken = savedToken;
            addErrorResult("Invalid method declaration");
        }
    }
    // 7. FuncDecl → Type ID ( ParameterList )
    private boolean funcDecl() {
        if (isType(currentToken.getText())) {
            String type = currentToken.getText();
            advance(); // Consume type
            if (currentToken.getType().equals("Identifier")) {
                advance(); // Consume ID
                if (matchText("(")) {
                    parameterList();
                    if (matchText(")")) {
                        return true; // Function declaration successfully parsed
                    } else {
                        addErrorResult("Expected ')' in function declaration");
                    }
                } else {
                    addErrorResult("Expected '(' in function declaration");
                }
            } else {
                addErrorResult("Expected identifier in function declaration");
            }
        } else {
            addErrorResult("'" + currentToken.getText() + "' is not a valid Type");
        }
        return false;
    }
    // 8. Type → Ire | Sire | Clo | SetOfClo | FBU | SFBU | None | Logical
    private boolean isType(String text) {
        return text.equals("Ire") || text.equals("Sire") || text.equals("Clo") || 
               text.equals("SetOfClo") || text.equals("FBU") || text.equals("SFBU") || 
               text.equals("None") || text.equals("Logical");
    }

    // 9. ParameterList → ε | None | NonEmptyParameterList
    private void parameterList() {
        if (currentTokenIndex < tokens.size() && currentToken.getText().equals("None")) {
            advance(); // Consume None
        } else if (currentTokenIndex < tokens.size() && isType(currentToken.getText())) {
            nonEmptyParameterList();
        }
        // ε case - do nothing
    }

    // 10. NonEmptyParameterList → Type ID | NonEmptyParameterList , Type ID
    private void nonEmptyParameterList() {
        if (isType(currentToken.getText())) {
            advance(); // Consume type
            if (currentToken.getType().equals("Identifier")) {
                advance(); // Consume ID
                if (currentTokenIndex < tokens.size() && currentToken.getText().equals(",")) {
                    advance(); // Consume comma
                    nonEmptyParameterList();
                }
            } else {
                addErrorResult("Expected identifier after type in parameter list");
            }
        } else {
            addErrorResult("Expected type in parameter list");
        }
    }

    // 11. VarDeclaration → ε | Type IDList ; VarDeclaration
    private void varDeclaration() {
        if (currentTokenIndex < tokens.size() && isType(currentToken.getText())) {
            String type = currentToken.getText();
            advance(); // Consume type
            idList();
            if (matchText(";")) {
                addMatchResult("VarDeclaration");
                varDeclaration();
            } else {
                addErrorResult("Expected ';' after variable declaration");
            }
        }
        // ε case - do nothing
    }

    // 12. IDList → ID | IDList , ID
    private void idList() {
        if (currentToken.getType().equals("Identifier")) {
            advance(); // Consume ID
            if (currentTokenIndex < tokens.size() && currentToken.getText().equals(",")) {
                advance(); // Consume comma
                idList();
            }
        } else {
            addErrorResult("Expected identifier in identifier list");
        }
    }

    // 13. Statements → ε | Statement Statements
    private void statements() {
        if (currentTokenIndex < tokens.size() && 
            !currentToken.getText().equals("}")) {
            statement();
            statements();
        }
        // ε case - do nothing
    }

    // 14. Statement → Assignment | WhetherDoStatement | RotateWhenStatement | 
    //                 ContinueWhenStatement | ReplyWithStatement | TerminateThisStatement |
    //                 read ( ID ) ; | write ( Expression ) ;
    private void statement() {
        int savedTokenIndex = currentTokenIndex;
        Token savedToken = currentToken;
    
        if (currentToken.getText().equals("WhetherDo")) {
            whetherDoStatement();
        } else if (currentToken.getText().equals("Rotatewhen")) {
            rotateWhenStatement();
        } else if (currentToken.getText().equals("Continuewhen")) {
            continueWhenStatement();
        } else if (currentToken.getText().equals("Replywith")) {
            replyWithStatement();
        } else if (currentToken.getText().equals("terminatethis")) {
            terminateThisStatement();
        } else if (currentToken.getText().equals("write") || currentToken.getText().equals("read")) {
            funcCall(); // Treat write/read as function calls
            if (matchText(";")) {
                addMatchResult("Statement");
            } else {
                addErrorResult("Expected ';' after write/read statement");
                skipToNextStatement();
            }
        } else if (currentToken.getType().equals("Identifier")) {
            advance(); // Consume ID
            if (currentToken.getText().equals("=")) {
                // It's an Assignment
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                assignment();
            } else if (currentToken.getText().equals("(")) {
                // It's a FuncCall
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                funcCall();
                if (matchText(";")) {
                    addMatchResult("Statement");
                } else {
                    addErrorResult("Expected ';' after function call");
                    skipToNextStatement();
                }
            } else {
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                addErrorResult("Invalid statement");
                skipToNextStatement();
            }
        } else if (isType(currentToken.getText())) {
            advance(); // Consume type
            if (currentToken.getType().equals("Identifier")) {
                advance(); // Consume ID
                if (currentToken.getText().equals("=")) {
                    // It's an Assignment
                    currentTokenIndex = savedTokenIndex;
                    currentToken = savedToken;
                    assignment();
                } else {
                    // It's a VarDeclaration
                    currentTokenIndex = savedTokenIndex;
                    currentToken = savedToken;
                    varDeclaration();
                }
            } else {
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                addErrorResult("Expected identifier after type");
                skipToNextStatement();
            }
        } else {
            addErrorResult("Invalid statement");
            skipToNextStatement();
        }
    }
    
    // 15. Assignment → VarDeclaration = Expression ;
    private void assignment() {
        int savedTokenIndex = currentTokenIndex;
        Token savedToken = currentToken;
    
        if (currentToken.getType().equals("Identifier")) {
            advance(); // Consume ID
            if (matchText("=")) {
                expression(); // Parse Expression, which can include FuncCall
                if (matchText(";")) {
                    addMatchResult("Assignment");
                } else {
                    addErrorResult("Expected ';' after assignment");
                    skipToNextStatement();
                }
            } else {
                addErrorResult("Expected '=' in assignment");
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
            }
        } else if (isType(currentToken.getText())) {
            varDeclaration();
            if (currentToken.getText().equals("=")) {
                advance(); // Consume '='
                expression();
                if (matchText(";")) {
                    addMatchResult("Assignment");
                } else {
                    addErrorResult("Expected ';' after assignment");
                    skipToNextStatement();
                }
            } else {
                // VarDeclaration without assignment is valid
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                varDeclaration();
            }
        } else {
            addErrorResult("Expected identifier or type for assignment");
            skipToNextStatement();
        }
    }
    
    private void skipToNextStatement() {
        while (currentTokenIndex < tokens.size() && 
               !currentToken.getText().equals(";") && 
               !currentToken.getText().equals("}") && 
               !currentToken.getText().equals("{")) {
            advance();
        }
        if (currentTokenIndex < tokens.size() && currentToken.getText().equals(";")) {
            advance(); // Consume semicolon
        }
    }
    // 16. FuncCall → ID ( ArgumentList ) ;    
    private void funcCall() {
        if (currentToken.getType().equals("Identifier")) {
            advance(); // Consume ID
            if (matchText("(")) {
                argumentList();
                if (matchText(")")) {
                    addMatchResult("FuncCall");
                } else {
                    addErrorResult("Expected ')' in function call");
                    skipToNextStatement();
                }
            } else {
                addErrorResult("Expected '(' in function call");
                skipToNextStatement();
            }
        } else {
            addErrorResult("Expected identifier for function call");
            skipToNextStatement();
        }
    }
    // 17. ArgumentList → ε | NonEmptyArgumentList
    private void argumentList() {
        if (currentTokenIndex < tokens.size() && !currentToken.getText().equals(")")) {
            expression();
            while (currentTokenIndex < tokens.size() && currentToken.getText().equals(",")) {
                advance(); // Consume ','
                expression();
            }
        }
        // ε case - do nothing if empty
    }
    // 18. NonEmptyArgumentList → Expression | NonEmptyArgumentList , Expression
    
    private void nonEmptyArgumentList() {
        expression();
        if (currentTokenIndex < tokens.size() && currentToken.getText().equals(",")) {
            advance(); // Consume comma
            nonEmptyArgumentList();
        }
    }

    // 19. BlockStatements → { Statements }
    private void blockStatements() {
        if (matchText("{")) {
            statements();
            if (matchText("}")) {
                // Block statements successfully parsed
            } else {
                addErrorResult("Expected '}'");
            }
        } else {
            addErrorResult("Expected '{'");
        }
    }

    // 20. WhetherDoStatement → WhetherDo ( ConditionExpression ) BlockStatements
    private void whetherDoStatement() {
        if (currentToken.getText().equals("WhetherDo")) {
            advance(); // Consume 'WhetherDo'
            if (matchText("(")) {
                condition();
                if (matchText(")")) {
                    if (matchText("{")) {
                        statements();
                        if (matchText("}")) {
                            addMatchResult("WhetherDoStatement");
                        } else {
                            addErrorResult("Expected '}' after WhetherDo block");
                            skipToNextStatement();
                        }
                    } else {
                        addErrorResult("Expected '{' after WhetherDo condition");
                        skipToNextStatement();
                    }
                } else {
                    addErrorResult("Expected ')' in WhetherDo statement");
                    skipToNextStatement();
                }
            } else {
                addErrorResult("Expected '(' in WhetherDo statement");
                skipToNextStatement();
            }
        } else {
            addErrorResult("Expected 'WhetherDo' for WhetherDo statement");
            skipToNextStatement();
        }
    }
    // 21. ConditionExpression → Condition | Condition ConditionOp Condition
    private void conditionExpression() {
        condition();
        if (currentTokenIndex < tokens.size() && 
            (currentToken.getText().equals("&&") || currentToken.getText().equals("||"))) {
            conditionOp();
            condition();
        }
    }

    // 22. ConditionOp → and | or
    private void conditionOp() {
        if (currentToken.getText().equals("and") || currentToken.getText().equals("or")) {
            advance(); // Consume the operator
            addMatchResult("ConditionOp");
        } else {
            addErrorResult("Expected condition operator ('and' or 'or')");
        }
    }
    // 23. Condition → Expression ComparisonOp Expression
    private void condition() {
        expression();
        comparisonOp();
        expression();
    }

    // 24. ComparisonOp → == | != | > | >= | < | <=
    private void comparisonOp() {
        if (currentToken.getText().equals("==") || currentToken.getText().equals("!=") ||
            currentToken.getText().equals(">") || currentToken.getText().equals(">=") ||
            currentToken.getText().equals("<") || currentToken.getText().equals("<=")) {
            advance(); // Consume comparison operator
        } else {
            addErrorResult("Expected comparison operator");
        }
    }

    // 25. RotateWhenStatement → RotateWhen ( ConditionExpression ) BlockStatements
    private void rotateWhenStatement() {
        if (matchText("Rotatewhen")) {
            if (matchText("(")) {
                conditionExpression();
                if (matchText(")")) {
                    blockStatements();
                    addMatchResult("RotateWhenStatement");
                } else {
                    addErrorResult("Expected ')'");
                }
            } else {
                addErrorResult("Expected '('");
            }
        } else {
            addErrorResult("Expected 'Rotatewhen'");
        }
    }

    // 26. ContinueWhenStatement → ContinueWhen ( Expression ; Expression ; Expression ) BlockStatements
    private void continueWhenStatement() {
        if (matchText("Continuewhen")) {
            if (matchText("(")) {
                expression();
                if (matchText(";")) {
                    expression();
                    if (matchText(";")) {
                        expression();
                        if (matchText(")")) {
                            blockStatements();
                            addMatchResult("ContinueWhenStatement");
                        } else {
                            addErrorResult("Expected ')'");
                        }
                    } else {
                        addErrorResult("Expected ';'");
                    }
                } else {
                    addErrorResult("Expected ';'");
                }
            } else {
                addErrorResult("Expected '('");
            }
        } else {
            addErrorResult("Expected 'Continuewhen'");
        }
    }

    // 27. ReplyWithStatement → ReplyWith Expression ; | return ID ;
    private void replyWithStatement() {
        if (matchText("Replywith")) {
            expression();
            if (matchText(";")) {
                addMatchResult("ReplyWithStatement");
            } else {
                addErrorResult("Expected ';'");
            }
        } else if (matchText("return")) {
            if (currentToken.getType().equals("Identifier")) {
                advance(); // Consume ID
                if (matchText(";")) {
                    addMatchResult("ReplyWithStatement");
                } else {
                    addErrorResult("Expected ';'");
                }
            } else {
                addErrorResult("Expected identifier after 'return'");
            }
        } else {
            addErrorResult("Expected 'Replywith' or 'return'");
        }
    }

    // 28. TerminateThisStatement → TerminateThis ;
    private void terminateThisStatement() {
        if (matchText("terminatethis")) {
            if (matchText(";")) {
                addMatchResult("TerminateThisStatement");
            } else {
                addErrorResult("Expected ';'");
            }
        } else {
            addErrorResult("Expected 'terminatethis'");
        }
    }

    // 29. Expression → Term | Expression AddOp Term
    private void expression() {
        term();
        while (currentTokenIndex < tokens.size() && 
               (currentToken.getText().equals("+") || currentToken.getText().equals("-"))) {
            addOp();
            term();
        }
    }
    // 30. AddOp → + | -
    private void addOp() {
        if (currentToken.getText().equals("+") || currentToken.getText().equals("-")) {
            advance(); // Consume operator
        } else {
            addErrorResult("Expected '+' or '-'");
        }
    }

    // 31. Term → Factor | Term MulOp Factor
    private void term() {
        factor();
        while (currentTokenIndex < tokens.size() && 
               (currentToken.getText().equals("*") || currentToken.getText().equals("/"))) {
            mulOp();
            factor();
        }
    }
    
    // 32. MulOp → * | /
    private void mulOp() {
        if (currentToken.getText().equals("*") || currentToken.getText().equals("/")) {
            advance(); // Consume operator
        } else {
            addErrorResult("Expected '*' or '/'");
        }
    }

    // 33. Factor → ID | Number
    private void factor() {
        if (currentToken.getType().equals("Identifier")) {
            int savedTokenIndex = currentTokenIndex;
            Token savedToken = currentToken;
            advance(); // Consume ID
            if (currentTokenIndex < tokens.size() && currentToken.getText().equals("(")) {
                // It's a function call
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                funcCall();
            } else {
                // It's just an identifier
                currentTokenIndex = savedTokenIndex;
                currentToken = savedToken;
                advance(); // Consume ID
                addMatchResult("Factor");
            }
        } else if (currentToken.getType().equals("Constant")) {
            advance(); // Consume Number or string literal
            addMatchResult("Factor");
        } else if (currentToken.getText().equals("(")) {
            advance(); // Consume '('
            expression();
            if (matchText(")")) {
                addMatchResult("Factor");
            } else {
                addErrorResult("Expected ')' in expression");
                skipToNextStatement();
            }
        } else {
            addErrorResult("Expected identifier, constant, or '(' in expression");
            skipToNextStatement();
        }
    }

    // 34. Comment → /## STR ##/ | /- STR
    private void comment() {
        if (currentToken.getText().equals("/##")) {
            advance(); // Consume /##
            // Skip until ##/
            while (currentTokenIndex < tokens.size() && !currentToken.getText().equals("##/")) {
                advance();
            }
            if (matchText("##/")) {
                // Comment successfully parsed
            } else {
                addErrorResult("Unterminated multi-line comment");
            }
        } else if (currentToken.getText().equals("/-")) {
            advance(); // Consume /-
            // Skip until end of line
            while (currentTokenIndex < tokens.size() && !currentToken.getText().equals(";")) {
                advance();
            }
            // Comment successfully parsed
        } else {
            addErrorResult("Expected '/##' or '/-' for comment");
        }
    }

    // 35. UsingCommand → using ( FName.txt ) ;
    private void usingCommand() {
        if (matchText("using")) {
            if (matchText("(")) {
                fName();
                if (matchText(")")) {
                    if (matchText(";")) {
                        // Using command successfully parsed
                    } else {
                        addErrorResult("Expected ';'");
                    }
                } else {
                    addErrorResult("Expected ')'");
                }
            } else {
                addErrorResult("Expected '('");
            }
        } else {
            addErrorResult("Expected 'using'");
        }
    }

    // 36. FName → STR
    private void fName() {
        if (currentToken.getType().equals("Identifier") || currentToken.getType().equals("Constant")) {
            advance(); // Consume filename
        } else {
            addErrorResult("Expected filename");
        }
    }
}