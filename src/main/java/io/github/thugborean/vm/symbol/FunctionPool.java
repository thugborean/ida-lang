package io.github.thugborean.vm.symbol;

import java.util.HashMap;
import java.util.Map;

public class FunctionPool {
    Map<String, Function> functionPool = new HashMap<>();
    public final String entryName = "main";

    public Function getFunction(String identifier) {
        if(functionPool.containsKey(identifier))
            return functionPool.get(identifier);
        else {
            throw new RuntimeException("Function with identifier " + identifier + " does not exist!");
        }
    }

    // This is called first
    public void declareFunction(String identifier, Function function) {
        if(functionPool.containsKey(identifier)) throw new RuntimeException("Function " + identifier + " is already declared!");
        functionPool.put(identifier, function);
    }

    // This is called second
    public void declareFunctionBody(String identifier, Function function) {
        functionPool.put(identifier, function);
    }

    public boolean checkForEntry() {
        return functionPool.containsKey(entryName);
    }
}