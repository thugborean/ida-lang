package io.github.thugborean.vm.symbol;

public class Value {
    public Object value;

    public Value(Object value) {
        if(value instanceof Value) throw new RuntimeException("Cannot wrap a Value inside a Value!");
        this.value = value;
    }

    public int asNumber() {
        if(!(value instanceof Integer)) throw new RuntimeException("EXPECTED NUMBER");
        return (int)value;
    }

    public double asDouble() {
        if(!(value instanceof Double)) throw new RuntimeException("EXPECTED DOUBLE!");
        return (double)value;
    }

    public String asString() {
        if(!(value instanceof String)) throw new RuntimeException("EXPECTED STRING!");
        return (String)value;
    }

    public Character asChar() {
        if(!(value instanceof Character)) throw new RuntimeException("EXPECTED CHAR!");
        return (Character)value;
    }

    public Boolean asBool() {
        if(!(value instanceof Boolean)) throw new RuntimeException("EXPECTED BOOL!");
        return (boolean)value;
    }

    public Object raw() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    // This might not be elegant but it'll do
    public ValType getType() {
        if(value instanceof Integer) {
            return ValType.NUMBER;
        } else if(value instanceof Double) {
            return ValType.DOUBLE;
        } else if(value instanceof String) {
            return ValType.STRING;
        } else if(value instanceof Character) {
            return ValType.CHARACTER;
        } else if(value instanceof Boolean) {
            return ValType.BOOL;
        } else return ValType.NULL;
    }
}