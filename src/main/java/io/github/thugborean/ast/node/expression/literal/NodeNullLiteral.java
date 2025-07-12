package io.github.thugborean.ast.node.expression.literal;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeNullLiteral extends NodeLiteral{

    public NodeNullLiteral(Token token) {
        super(token);
    }
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
       return visitor.visitNodeNullLiteral(this);
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValue'");
    }
    
}
