package io.github.thugborean.vm;

import java.util.HashMap;
import java.util.Map;

import io.github.thugborean.vm.symbol.Symbol;
import io.github.thugborean.vm.symbol.Variable;

public class Environment {
    private Environment subEnv;
    private final Map<String, Variable> variables = new HashMap<>();
    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, Structure> structures = new HashMap<>();

    // Method used for retrieving the variable from the environment
    public Variable getVariable(String identifier) {
        Variable var = variables.get(identifier);
        if(var == null) throw new RuntimeException("Unknown Symbol: " + identifier);
        return var;
    }

    public void defineVariable(String identifier, Variable variable) {
        if(variables.containsKey(identifier)) throw new RuntimeException("Duplicate identifiers!");
        variables.put(identifier, variable);
    }

    public void assignVariable(String identifier, Variable variable) {
        if(variables.containsKey(identifier)) variables.put(identifier, variable);
            else throw new RuntimeException("Cannot assign to unknown Symbol: " + identifier + "the value of: " + variable.getvalue());
    }

    // ???
    public boolean variableExists(String identifier) {
        if(variables.containsKey(identifier)) return true;
            else return false;
    }
}


// These can stay unimplemented for now...
class Function implements Symbol {
    private final String type;
    private Object value;

    public Function(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Object getvalue() {
        return value;
    }

}
class Structure implements Symbol {
    private final String type;
    private Object value;
    
    public Structure(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Object getvalue() {
        return value;
    }
}