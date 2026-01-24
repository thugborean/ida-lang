package io.github.thugborean.vm.symbol;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.thugborean.ast.node.statement.NodeBlock;
import io.github.thugborean.ast.node.statement.NodeStatement;

public class Function implements Symbol {
    public final String identifier;
    public final ValType returnType;

    public Set<Param> parameters = new HashSet<>();
    public Set<Modifier> modifiers = new HashSet<>();
    public NodeBlock contents;    

    public Function(String identifier, ValType returnType, Set<Param> paramaters, Set<Modifier> modifiers, NodeBlock contents) {
        this.identifier = identifier;
        this.returnType = returnType;
        this.parameters = paramaters;
        this.modifiers = modifiers;
        this.contents = contents;
    }

    public void addContents(List<NodeStatement> contents) {
        
    }

    // When entering the block of a function we create a copy of the paramaters unless they are specified with a & WIP
    public ValType getType() {
        return this.returnType;
    }
}