package dope;

import java.util.List;

class Function implements Callable {
    private final Environment closure;
    private final Stmt.Function declaration;
    private final boolean isInitializer;

    Function(Stmt.Function declaration, Environment closure,
             boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
    }

    Function bind(Instance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new Function(declaration, environment,
                isInitializer);
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public Object call(Interpreter interpreter,
                       List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme,
                    arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");
            return returnValue.value;
        }

        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }
}