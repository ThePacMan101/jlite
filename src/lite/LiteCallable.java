package lite;

import java.util.List;

public interface LiteCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);    
}
