package io.github.thugborean.vm;

import io.github.thugborean.vm.symbol.*;

public class Environment {
    private final Environment parentEnv;
    private final SymbolTable symbolTable;
    private final int scopeLevel;

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

    public void assignVariable(String identifier, Value value) {
        symbolTable.setVariable(identifier, value, scopeLevel);
    }

    public boolean variableExists(String identifier) {
        return symbolTable.symbolExists(identifier);
    }
}