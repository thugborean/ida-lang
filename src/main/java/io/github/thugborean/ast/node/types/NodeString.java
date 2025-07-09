package io.github.thugborean.ast.node.types;

import io.github.thugborean.vm.symbol.ValType;

public class NodeString extends NodeType{
    public NodeString() {
        this.type = ValType.STRING;
    }
}