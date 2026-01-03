package lite;

abstract class Expr{

	abstract <T> T accept(Visitor<T> visitor);

	interface Visitor<T> {
		T visitBinaryExpr(Binary expr);
		T visitTernaryExpr(Ternary expr);
		T visitUnaryExpr(Unary expr);
		T visitGroupingExpr(Grouping expr);
		T visitLiteralExpr(Literal expr);
	}

	static class Binary extends Expr {
		Binary(Expr left, Token operator, Expr right){
			this.left=left;
			this.operator=operator;
			this.right=right;
		}
		final Expr left;
		final Token operator;
		final Expr right;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitBinaryExpr(this);
		}
	}
	static class Ternary extends Expr {
		Ternary(Expr left, Expr middle, Expr right){
			this.left=left;
			this.middle=middle;
			this.right=right;
		}
		final Expr left;
		final Expr middle;
		final Expr right;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitTernaryExpr(this);
		}
	}
	static class Unary extends Expr {
		Unary(Token operator, Expr right){
			this.operator=operator;
			this.right=right;
		}
		final Token operator;
		final Expr right;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitUnaryExpr(this);
		}
	}
	static class Grouping extends Expr {
		Grouping(Expr expression){
			this.expression=expression;
		}
		final Expr expression;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitGroupingExpr(this);
		}
	}
	static class Literal extends Expr {
		Literal(Object value){
			this.value=value;
		}
		final Object value;
		@Override
		<T> T accept(Visitor<T> visitor){
			return visitor.visitLiteralExpr(this);
		}
	}
}
