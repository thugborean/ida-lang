package io.github.thugborean.ast.node.types;

import io.github.thugborean.ast.node.NodeAST;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.vm.symbol.ValType;
public abstract class NodeType implements NodeAST{
    public ValType type;
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeType(this);
    }
}