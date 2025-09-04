package io.github.thugborean.vm;

import java.util.HashMap;
import java.util.Map;

import io.github.thugborean.vm.symbol.*;

public class Environment {
    private final Environment parentEnv;
    public final Map<String, Variable> localVariables = new HashMap<>();
    public final int scopeLevel;
    public static int globalScopeDepth = 0;


    public Environment(Environment parentEnv) {
        this.parentEnv = parentEnv;
        this.scopeLevel = globalScopeDepth;
        globalScopeDepth++;
    }

    public Environment() {
        parentEnv = null;
        this.scopeLevel = globalScopeDepth;
        globalScopeDepth++;
    }

    // This might not work...
    // Method used for retrieving the variable from the local environment
    public Variable getVariable(String identifier) {
        if(variableExists(identifier)) return localVariables.get(identifier);
            else if(parentEnv != null) {
                return parentEnv.getVariable(identifier);
            } else throw new RuntimeException("Variable with identifer " + identifier + "does not exist");
    }

    // If the variable identifier is not already used inside the local scope then we can declare it
    public void declareVariable(String identifier, Variable variable) {
        if(localVariables.containsKey(identifier)) throw new RuntimeException("Variable "+ identifier +" is already delcared in this scope");
        localVariables.put(identifier, variable);
    }

    public void assignVariable(String identifier, Value value) {
        Variable oldVariable;
        if(variableExists(identifier)) oldVariable = getVariable(identifier);
            else if(parentEnv != null) oldVariable = parentEnv.getVariable(identifier);
        else throw new RuntimeException("Variable " + identifier + " has not been declared in this scope");
        Variable newVariable = new Variable(oldVariable.getType(), value);
        localVariables.put(identifier, newVariable);
    }

    public boolean variableExists(String identifier) {
        return localVariables.containsKey(identifier);
    }

    // The idea is that when we remove this environment from the envStack then we've exited it and it will eventually be garbage collected
    public void close() {
        globalScopeDepth--;
    }
}