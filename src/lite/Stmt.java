package lite;

abstract class Stmt{

	abstract <T> T accept(Visitor<T> visitor);

	interface Visitor<T> {
		T visitExpressionStmt(Expression stmt);
		T visitPrintStmt(Print stmt);
		T visitVarStmt(Var stmt);
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
	static class Var extends Stmt {
		Var(Token name, Expr initializer){
			this.name=name;
			this.initializer=initializer;
		}
		final Token name;
		final Expr initializer;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitVarStmt(this);
		}
	}
}
