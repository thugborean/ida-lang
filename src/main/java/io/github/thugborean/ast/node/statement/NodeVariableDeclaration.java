package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeVariableDeclaration extends NodeStatement {
    public NodeType type;
    public Token identifier;
    public NodeExpression initialValue;

    public NodeVariableDeclaration(NodeType type, Token identifier, NodeExpression initialValue) {
        this.type = type;
        this.identifier = identifier;
        this.initialValue = initialValue;
    }
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeVariableDeclaration(this);
    }
}