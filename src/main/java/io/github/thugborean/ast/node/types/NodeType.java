package io.github.thugborean.ast.node.types;

import io.github.thugborean.ast.node.NodeAST;
import io.github.thugborean.ast.visitor.ASTVisitor;
public abstract class NodeType implements NodeAST{
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }
}