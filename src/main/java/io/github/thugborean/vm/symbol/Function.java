package io.github.thugborean.vm.symbol;

import java.util.HashSet;
import java.util.Set;

import io.github.thugborean.ast.node.statement.NodeBlock;

public class Function implements Symbol {
    public final String identifier;
    public final ValType returnType;

    public Set<Variable> parameters = new HashSet<>();
    public Set<Modifiers> modifiers = new HashSet<>();
    public NodeBlock contents;    

    public Function(String identifier, ValType returnType, Set<Variable> paramaters, Set<Modifiers> modifiers) {
        this.identifier = identifier;
        this.returnType = returnType;
        this.parameters = paramaters;
        this.modifiers = modifiers;
    }

    // When entering the block of a function we create a copy of the paramaters unless they are specified with a & WIP
    public ValType getType() {
        return this.returnType;
    }
}