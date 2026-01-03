package lite;

import java.util.List;

import lite.Expr.Assign;
import lite.Stmt.Expression;
import lite.Stmt.Print;
import lite.Stmt.Var;

class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String>{
    String print(Expr expr){
        return expr.accept(this);
    }

    String print(Stmt stmt){
        return stmt.accept(this);
    }

    String print(List<Stmt> stmts){
        StringBuilder builder = new StringBuilder();
        for (Stmt stmt : stmts){
            builder.append(print(stmt));
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public String visitTernaryExpr(Expr.Ternary expr) {
        return parenthesize("ternary", expr.left,expr.middle,expr.right);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme,expr.left,expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme,expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr){
        return expr.name.lexeme;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group",expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.value==null) return "nil";
        return expr.value.toString();
    }        

    @Override
    public String visitExpressionStmt(Expression stmt) {
        return parenthesize("expression",stmt.expression);
    }

    @Override
    public String visitPrintStmt(Print stmt) {
        return parenthesize("print",stmt.expression);
    }

    @Override
    public String visitVarStmt(Var stmt) {
        if(stmt.initializer == null) 
            return parenthesize("var "+stmt.name.lexeme);
        else
            return parenthesize("var "+stmt.name.lexeme+" =",stmt.initializer);
    }

    private String parenthesize(String name, Expr... exprs){
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for(Expr expr : exprs){
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitAssignExpr(Assign expr) {
        return parenthesize(expr.name.lexeme+" =",expr.value);
    }
}