package lite;

import static lite.Stmt.*;

abstract class StmtVisitor{
	interface Visitor<T> {
		T visitExpressionStmt(Expression stmt);
		T visitPrintStmt(Print stmt);
	}
}
