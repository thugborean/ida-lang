package io.github.thugborean.vm.symbol;

public class Value {
    private Object value;

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
}