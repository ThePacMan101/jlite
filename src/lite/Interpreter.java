package lite;

import lite.Expr.Binary;
import lite.Expr.Grouping;
import lite.Expr.Literal;
import lite.Expr.Unary;
import java.lang.Math;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left  = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left,right);   
                return (double)left - (double)right;
            case PLUS:  
                if(left instanceof Double && right instanceof Double){
                    return (double)left + (double)right;
                }else if(left instanceof String && right instanceof String){
                    return (String)left + (String)right;
                }else if(left instanceof String && right instanceof Double){
                    String stringified = right.toString();
                    if(stringified.endsWith(".0"))
                        stringified = stringified.substring(0,stringified.length()-2);
                    return (String)left + stringified;
                }else if(left instanceof Double && right instanceof String){
                    String stringified = left.toString();
                    if(stringified.endsWith(".0"))
                        stringified = stringified.substring(0,stringified.length()-2);
                    return stringified + (String)right;
                }
                throw new RuntimeError(expr.operator,"Operands must be two numbers or two strings.");
            case SLASH: 
                checkNumberOperands(expr.operator, left,right);
                return (double)left / (double)right;
            case STAR:  
                checkNumberOperands(expr.operator, left,right);
                return (double)left * (double)right;
        
            case GREATER:       
                checkNumberOperands(expr.operator, left,right);    
                return (double)left >  (double)right;
            case GREATER_EQUAL: 
                checkNumberOperands(expr.operator, left,right);    
                return (double)left >= (double)right;
            case LESS:          
                checkNumberOperands(expr.operator, left,right);    
                return (double)left <  (double)right;
            case LESS_EQUAL:    
                checkNumberOperands(expr.operator, left,right);    
                return (double)left <= (double)right;

            case OR:
                if(isTruthy(left)) return left;
                if(isTruthy(right)) return right;
                return false;
                // return isTruthy(left)||isTruthy(right);
            case AND:
                if(isTruthy(left)) if(isTruthy(right)) return right;
                return false;
                // return isTruthy(left)&&isTruthy(right);
            
            case BIT_AND:
                checkRoundNumberOperands(expr.operator, left, right);
                return (double)((int)Math.round((double)left) & (int)Math.round((double)right));
            case BIT_OR:
                checkRoundNumberOperands(expr.operator, left, right);
                return (double)((int)Math.round((double)left) | (int)Math.round((double)right));
            case BIT_XOR:
                checkRoundNumberOperands(expr.operator, left, right);
                return (double)((int)Math.round((double)left) ^ (int)Math.round((double)right));

            case BANG_EQUAL:    return !isEqual(left,right);
            case EQUAL_EQUAL:   return isEqual(left,right);

            default:
                break;
        }
        // unreachable
        return null;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);  
                return -(double)right;
            case BIT_NOT:
                checkRoundNumberOperand(expr.operator, right);  
                return (double)(~(int)Math.round((double)right));
        }

        // unreachable
        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt){
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }
    
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt){
        evaluate(stmt.expression);
        return null;
    }

    private Object evaluate(Expr expr){
        return expr.accept(this);
    }
    private boolean isTruthy(Object object){
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean)object;
        if(object instanceof Double) return (double)object != 0;
        if(object instanceof String) return !((String)object).isEmpty();
        return true;
    }
    private boolean isEqual(Object a,Object b){
        if(a==null && b==null) return true;
        if(a==null) return false;

        return a.equals(b);
    }
    private void checkNumberOperand(Token operator, Object operand){
        if(operand instanceof Double) return;
        throw new RuntimeError(operator,"Operand must be a number.");
    }
    private void checkRoundNumberOperand(Token operator, Object operand){
        if(operand instanceof Double && ((double)operand - (double)Math.round((double)operand) == 0)
        ) return;
        throw new RuntimeError(operator,"Operand must be a round number.");
    }
    private void checkRoundNumberOperands(Token operator, Object left, Object right){
        if( (left  instanceof Double && ((double)left  - (double)Math.round((double)left ) == 0))
        &&  (right instanceof Double && ((double)right - (double)Math.round((double)right) == 0))
        ) return;
        throw new RuntimeError(operator,"Operands must be round numbers.");
    }
    private void checkNumberOperands(Token operator, Object left,Object right){
        if( (left  instanceof Double || left  instanceof Integer)
        &&  (right instanceof Double || right instanceof Integer) )
            return;
            
        throw new RuntimeError(operator,"Operands must be numbers.");

    }
    private String stringify(Object object){
        if(object == null) return "nil";
        if(object instanceof Double){
            String text = object.toString();
            if(text.endsWith(".0"))
                text = text.substring(0,text.length()-2);
            return text;
        }
        return object.toString();
    }
    
    void interpret(List<Stmt> statements){
        try{
            for(Stmt statement : statements){
                execute(statement);
            }
        }catch(RuntimeError error){
            Lite.runtimeError(error);
        }
    }

    private void execute(Stmt stmt){
        stmt.accept(this);
    }
    

}
