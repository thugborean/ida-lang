package io.github.thugborean.vm;

import java.util.HashMap;
import java.util.Map;

import io.github.thugborean.vm.symbol.*;

public class Environment {
    private final Environment parentEnv;
    private final SymbolTable symbolTable;
    private final int scopeLevel;

    private final Map<String, Variable> variables = new HashMap<>();


    public Environment(Environment parentEnv, SymbolTable symbolTable, int scopeLevel) {
        this.parentEnv = parentEnv;
        this.symbolTable = symbolTable;
        this.scopeLevel = scopeLevel;
    }

    // Method used for retrieving the variable from the symbolTable
    public Variable getVariable(String identifier) {
        return symbolTable.getVariable(identifier, scopeLevel);
    }

    public void declareVariable(String identifier, Variable variable) {
        symbolTable.declare(identifier, variable);
    }

    public void assignVariable(String identifier, Variable variable) {
        if(variables.containsKey(identifier)) variables.put(identifier, variable);
            else throw new RuntimeException("Cannot assign to unknown Symbol: " + identifier + " the value of: " + variable.getvalue());
    }

    // ???
    public boolean variableExists(String identifier) {
        if(variables.containsKey(identifier)) return true;
            else return false;
    }
}