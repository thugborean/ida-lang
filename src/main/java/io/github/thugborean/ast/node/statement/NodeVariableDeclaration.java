package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.vm.symbol.ValType;

public class NodeVariableDeclaration extends NodeStatement {
    public ValType type;
    public String identifier;
    public NodeExpression initializer;

    public NodeVariableDeclaration(ValType type, String identifier, NodeExpression initializer) {
        this.type = type;
        this.identifier = identifier;
        this.initializer = initializer;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeVariableDeclaration(this);
    }
}