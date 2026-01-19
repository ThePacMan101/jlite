package lite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static lite.TokenType.*;

class Parser{
    private static class ParseError extends RuntimeException{}
    
    private final List<Token> tokens;
    private int current = 0 ;
    private int loopDepth = 0 ;


    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        while(!isAtEnd()){
            statements.add(declaration());
        }
        return statements;
    }
    private Stmt declaration(){
        try{
            if(match(FN))  return function("function");
            if(match(VAR)) return varDecl();
            return statement();
        } catch (ParseError error){
            synchronize();
            return null;
        }
    }
    private Stmt varDecl(){
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if(match(EQUAL)){
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }
    private Stmt.Function function(String kind){
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after "+kind+" name.");
        List<Token> parameters = new ArrayList<>();
        if(!check(RIGHT_PAREN)){
            do{
                if(parameters.size()>=255){
                    error(peek(), "Can't have more than 255 parameters.");
                }
                parameters.add(consume(IDENTIFIER,"Expect parameter name."));
            }while(match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before "+kind+" body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }
    private Stmt statement(){
        if(match(BREAK)) return breakStatement();
        if(match(FOR)) return forStatement();
        if(match(WHILE)) return whileStatement();
        if(match(IF)) return ifStatement();
        if(match(PRINT)) return printStatement();
        if(match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }
    private Stmt printStatement(){
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }
    private Stmt expressionStatement(){
        Expr value = expression();
        consume(SEMICOLON,  "Expect ';' after value.");
        return new Stmt.Expression(value);
    }
    private List<Stmt> block(){
        List<Stmt> statements = new ArrayList<>();
        while(!check(RIGHT_BRACE) && !isAtEnd()){
            statements.add(declaration());
        }
        consume(RIGHT_BRACE,"Expect '}' after block.");
        return statements;
    }
    private Stmt ifStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if(match(ELSE)){
            elseBranch = statement();
        }
        return new Stmt.If(condition,thenBranch,elseBranch);
    }
    private Stmt whileStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after while condition.");
        
        Stmt body;
        try{
            loopDepth++;
            body = statement();
        }finally{
            loopDepth--;
        }

        return new Stmt.While(condition, body);
    }
    private Stmt forStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Stmt initializer;
        if(match(SEMICOLON)) initializer = null;
        else if(match(VAR)) initializer = varDecl();
        else initializer = expressionStatement();

        Expr condition = null;
        if(!check(SEMICOLON)) condition = expression();
        consume(SEMICOLON, "Expect ';' after for loop condition");
        Expr increment = null;
        if(!check(RIGHT_PAREN)) increment = expression();
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");
        
        Stmt body;
        try{
            loopDepth++;
            body = statement();
        }finally{
            loopDepth--;
        }
        
        if(increment!=null){
            body = new Stmt.Block(Arrays.asList(body,new Stmt.Expression(increment)));
        }

        if(condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if(initializer!=null){
            body = new Stmt.Block(Arrays.asList(initializer,body));
        }

        return body;
    }
    private Stmt breakStatement(){
        Token breakToken= previous();
        consume(SEMICOLON, "Expect ';' after 'break'.");
        if(loopDepth <= 0) throw error(breakToken, "Cannot use 'break' statement outside of a loop.");
        return new Stmt.Break();
    }
    private Expr expression(){
        return assignment();
    }
    private Expr assignment(){
        Expr expr = equality();
        
        if(match(EQUAL)){
            Token equals = previous();
            Expr value   = assignment();

            if(expr instanceof Expr.Variable){
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }
            
            error(equals,"Invalid assignment target.");
        }
        return expr;
    }
    private Expr equality(){
        Expr expr = logComparison();
        while(match(EQUAL_EQUAL,BANG_EQUAL)){
            Token operator = previous();
            Expr right = logComparison();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }
    private Expr logComparison(){
        Expr expr = bitComparison();
        while(match(AND,OR)){
            Token operator = previous();
            Expr right = bitComparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr bitComparison(){
        Expr expr = comparison();
        while(match(BIT_AND,BIT_XOR,BIT_OR)){
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr comparison(){
        Expr expr = term();
        while(match(LESS,GREATER,LESS_EQUAL,GREATER_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr term(){
        Expr expr = factor();
        while(match(MINUS,PLUS)){
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr factor(){
        Expr expr = unary();
        while(match(STAR,SLASH)){
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr unary(){
        if(match(BANG,MINUS,BIT_NOT)){
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator,right);
        }
        return call();
    }
    private Expr call(){
        Expr expr = primary();

        while(true){
            if(match(LEFT_PAREN)) {
                expr = finishCall(expr);
            }else{
                break;
            }
        }

        return expr;
    }
    private Expr finishCall(Expr callee){   // sorta like the arguments grammar rule
        List<Expr> arguments = new ArrayList<>();
        if(!check(RIGHT_PAREN)){
            do {
                if(arguments.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }
                arguments.add(expression());
            }while(match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN,"Expect ')' after arguments.");
        return new Expr.Call(callee, paren,arguments);
    }
    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);
        if(match(NUMBER,STRING)) return new Expr.Literal(previous().literal);
        if(match(IDENTIFIER)) return new Expr.Variable(previous());
    

        if(match(LEFT_PAREN)){
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            if(match(QUESTION_MARK)){
                Expr middle = expression();
                consume(COLON, "Expect ':' after expression.");
                Expr right = expression();
                return new Expr.Ternary(expr,middle,right);
            }
            return new Expr.Grouping(expr);
        }

        throw error(peek(),"Expected expression.");
    }

    private boolean match(TokenType... types){
        for(TokenType type: types){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }
    private boolean check(TokenType type){
        if(isAtEnd()) return false;
        return tokens.get(current).type == type;
    }
    private Token peek(){
        return tokens.get(current);
    }
    private boolean isAtEnd(){
        return peek().type == EOF;
    }
    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }
    private Token previous(){
        return tokens.get(current-1);
    }
    private Token consume(TokenType type,String message){
        if(check(type)) return advance();
        throw error(peek(),message);
    }

    private ParseError error(Token token, String message){
        Lite.error(token,message);
        return new ParseError();
    }
    private void synchronize(){
        advance();
        while(!isAtEnd()){
            if(previous().type==SEMICOLON) return ;
            
            switch (peek().type) {
                case CLASS:
                case FN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
                default: // supress warning
            }

            advance();
        }
    }
}