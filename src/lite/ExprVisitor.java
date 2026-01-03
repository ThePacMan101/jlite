package lite;

import static lite.Expr.*;

abstract class ExprVisitor{
	interface Visitor<T> {
		T visitBinaryExpr(Binary expr);
		T visitTernaryExpr(Ternary expr);
		T visitUnaryExpr(Unary expr);
		T visitGroupingExpr(Grouping expr);
		T visitLiteralExpr(Literal expr);
	}
}
