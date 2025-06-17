package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.syntax.Token;

public class NodeVariableDeclaration extends NodeStatement {
    private Token token;
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // TODO Auto-generated method stub
        return null;
    }
}
