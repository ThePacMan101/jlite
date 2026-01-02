package lite;

abstract class Stmt{

	abstract <T> T accept(Visitor<T> visitor);

	interface Visitor<T> {
		T visitExpressionStmt(Expression stmt);
		T visitPrintStmt(Print stmt);
	}

	static class Expression extends Stmt {
		Expression(Expr expression){
			this.expression=expression;
		}
		final Expr expression;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitExpressionStmt(this);
		}
	}
	static class Print extends Stmt {
		Print(Expr expression){
			this.expression=expression;
		}
		final Expr expression;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitPrintStmt(this);
		}
	}
}
