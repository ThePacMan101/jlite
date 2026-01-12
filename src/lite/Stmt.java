package lite;

import java.util.List;

abstract class Stmt{

	abstract <T> T accept(Visitor<T> visitor);

	interface Visitor<T> {
		T visitBlockStmt(Block stmt);
		T visitExpressionStmt(Expression stmt);
		T visitPrintStmt(Print stmt);
		T visitVarStmt(Var stmt);
	}

	static class Block extends Stmt {
		Block(List<Stmt> statements){
			this.statements=statements;
		}
		final List<Stmt> statements;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitBlockStmt(this);
		}
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
