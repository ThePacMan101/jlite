package lite;

import java.util.List;

public class LiteFunction implements LiteCallable{
    private final Stmt.Function declaration;
    private final Environment closure;

    LiteFunction(Stmt.Function declaration, Environment closure){
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments){
        Environment environment = new Environment(this.closure);
        for(int i = 0 ; i < declaration.params.size() ; ++i){
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }
        try{
            interpreter.executeBlock(declaration.body,environment);
        }catch(Return returnValue){
            return returnValue.value;
        }
        return null;
    }
    @Override
    public int arity(){
        return declaration.params.size();
    }
    @Override
    public String toString(){
        return "<function "+declaration.name.lexeme+">";
    }
}
