package io.github.thugborean.ast.node.expression;

import java.util.ArrayList;

import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeFunctionCall extends NodeExpression{

    public String identifier;
    public List<NodeExpression> parameters = new ArrayList<>();
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }
    
}
