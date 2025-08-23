package io.github.thugborean.vm.symbol;

import java.util.ArrayList;
import java.util.List;

// This class is responsible for storing all the variables in all of the environments
public class SymbolTable {
    private final List<Entry> entries = new ArrayList<>();
    private int currentScope = 0;

    public void declare(String identifier, Symbol symbol) {
        entries.add(new Entry(identifier, symbol, currentScope));
    }

    public Entry lookup(String name) {
        return entries.stream()
            .filter(e -> e.identifier.equals(name))
            .reduce((first, second) -> second) // Pick the one in the innermost scope
            .orElseThrow(() -> new RuntimeException("Unknown variable: " + name));
    }

    public void setVariable(String identifier, Value value, int scopeLevel) {
        Entry entry = lookup(identifier);
        if(entry.scopeLevel > scopeLevel) throw new RuntimeException("Symbol is unknown in current scope");
        if(!(entry.symbol instanceof Variable)) throw new RuntimeException("Trying to set the value of a non-variable symbol");
        entry.symbol.setValue(value);
    }

    public Variable getVariable(String identifier, int scopeLevel) {
        Entry entry = get(identifier, scopeLevel);
        if(!(entry.symbol instanceof Variable)) throw new RuntimeException("Trying to get a non-variable symbol");
        return (Variable)entry.symbol;
    }

    public Entry get(String identifier, int scopeLevel) {
        Entry entry = lookup(identifier);
        if(entry.scopeLevel > scopeLevel) throw new RuntimeException("Symbol is unknown in current scope");
        return entry;
    }

    public void enterScope() {currentScope++;}

    public void exitScope() {
        // Removes all entries when exiting the current level
        entries.removeIf(e -> e.scopeLevel == currentScope);
        currentScope--;
    }
}

class Entry {
    public String identifier;
    public Symbol symbol; // Can be either variable, function or struct -- WIP
    public int scopeLevel;

    public Entry(String identifier, Symbol symbol, int scopeLevel) {
        this.identifier = identifier;
        this.symbol = symbol;
        this.scopeLevel = scopeLevel;
    }
}