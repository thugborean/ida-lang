package io.github.thugborean.vm.symbol;

public class Variable {

    private final ValType type;
    private Value value;

    public Variable(ValType type, Value value) {
        this.type = type;
        this.value = value;
    }

    public ValType getType() {
        return type;
    }

    public Value getvalue() {
        return value;
    }
}