package lite;

import static lite.Stmt.*;

public class StmtVisitor {
    interface Visitor<T> {
        T visitExpressionStmt(Expression stmt);
        T visitPrintStmt(Print stmt);
    } 
}
