package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeVariableDeclaration extends NodeStatement {
    public Token identifier;
    public NodeType type;
    public NodeExpression initialValue;

    public NodeVariableDeclaration(Token identifier, NodeType type, NodeExpression initialValue) {
        this.identifier = identifier;
        this.type = type;
        this.initialValue = initialValue;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeVariableDeclaration(this);
    }
}
