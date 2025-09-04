package io.github.thugborean.ast.node.statement.scope;

import io.github.thugborean.ast.node.statement.NodeStatement;
import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeEnterScope extends NodeStatement {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeEnterScope(this);
    }
}