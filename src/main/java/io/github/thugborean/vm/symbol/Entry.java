package io.github.thugborean.vm.symbol;

public class Entry {
    public String identifier;
    public Symbol symbol; // Can be either variable, function or struct -- WIP
    public int scopeLevel;

    public Entry(String identifier, Symbol symbol, int scopeLevel) {
        this.identifier = identifier;
        this.symbol = symbol;
        this.scopeLevel = scopeLevel;
    }
}