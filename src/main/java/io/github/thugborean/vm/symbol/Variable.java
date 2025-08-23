package io.github.thugborean.vm.symbol;

public class Variable implements Symbol{

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
        if(value != null)
            return value;
        else return null;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}