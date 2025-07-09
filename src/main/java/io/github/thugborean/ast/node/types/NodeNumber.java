package io.github.thugborean.ast.node.types;

import io.github.thugborean.vm.symbol.ValType;

public class NodeNumber extends NodeType{
    public NodeNumber() {
        this.type = ValType.NUMBER;
    }
}