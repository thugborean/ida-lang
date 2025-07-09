package io.github.thugborean.ast.node.types;

import io.github.thugborean.vm.symbol.ValType;

public class NodeDouble extends NodeType{
    public NodeDouble() {
        this.type = ValType.DOUBLE;
    }
}