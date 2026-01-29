package io.github.thugborean.ast.node.expression;

import java.util.ArrayList;
import java.util.List;

import io.github.thugborean.ast.visitor.ASTVisitor;

public class NodeFunctionCall extends NodeExpression{
    public String identifier;
    public List<NodeExpression> arguments = new ArrayList<>();

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeFunctionCall(this);
    }
    
}
