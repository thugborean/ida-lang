package io.github.thugborean.ast.node.types;

import io.github.thugborean.ast.node.NodeAST;
import io.github.thugborean.ast.visitor.ASTVisitor;
public abstract class NodeType implements NodeAST{
    public String type;
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeType(this);
    }
}