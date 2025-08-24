package io.github.thugborean.vm.symbol;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Deque;
import java.util.HashMap;

// This class is responsible for storing all the variables in all of the environments
public class SymbolTable {
    private final Map<String, Deque<Entry>> table = new HashMap<>();
    private int currentScope = 0;

    public void declare(String identifier, Symbol symbol) {
        table.computeIfAbsent(identifier, k -> new ArrayDeque<>())
        .push(new Entry(identifier, symbol, currentScope));
    }

    public Entry lookup(String identifier) {
        Deque<Entry> stack = table.get(identifier);
        if (stack == null || stack.isEmpty()) {
            throw new RuntimeException("Unknown variable: " + identifier);
        }
        return stack.peek(); // always the innermost scope
    }

    public boolean symbolExists(String identifier) {
        Deque<Entry> stack = table.get(identifier);
        return stack != null && !stack.isEmpty();
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
        // Remove all entries in this scope
        for (Deque<Entry> stack : table.values()) {
            while (!stack.isEmpty() && stack.peek().scopeLevel == currentScope) {
                stack.pop();
            }
        }
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