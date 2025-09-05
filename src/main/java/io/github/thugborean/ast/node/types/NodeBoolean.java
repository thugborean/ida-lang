package io.github.thugborean.ast.node.types;

import io.github.thugborean.vm.symbol.ValType;

public class NodeBoolean extends NodeType{
    public NodeBoolean() {
        this.type = ValType.BOOLEAN;
    }
}
