package lite;

import java.util.ArrayList;
import java.util.List;
import static lite.TokenType.*;
import lite.Stmt;
import lite.Stmt.Expression;

class Parser{
    private static class ParseError extends RuntimeException{}
    
    private final List<Token> tokens;
    private int current = 0 ;


    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        while(!isAtEnd()){
            statements.add(statement());
        }
        return statements;
    }

    private Stmt statement(){
        if(match(PRINT)) return printStatement();
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
    private Expr expression(){
        return equality();
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
        return primary();
    }
    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);
        if(match(NUMBER,STRING)) return new Expr.Literal(previous().literal);

        // maybe change this later
        // if(match(IDENTIFIER)) return new Expr.Literal(previous().lexeme);

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
            }

            advance();
        }
    }
}