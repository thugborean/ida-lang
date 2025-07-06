package io.github.thugborean.vm.symbol;

public class Variable {

    private final String type;
    private Value value;

    public Variable(String type, Value value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Value getvalue() {
        return value;
    }
}