package io.github.thugborean.vm;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Environment subEnv;
    private final Map<String, Variable> variables = new HashMap();
    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, Function> structures = new HashMap<>();
}

class Variable implements Symbol {
    public String getName() {
        return null;
    }
    public String getType() {
        return null;
    }
}

class Function implements Symbol {
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }

}

class Structure implements Symbol {
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }
}