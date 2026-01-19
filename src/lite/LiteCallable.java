package lite;

import java.util.List;

public interface LiteCallable {
    Object call(Interpreter interpreter, List<Object> arguments);    
}
