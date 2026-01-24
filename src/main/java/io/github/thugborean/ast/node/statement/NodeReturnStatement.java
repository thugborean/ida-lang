package io.github.thugborean.ast.node.statement;

import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.vm.symbol.ValType;

public class NodeReturnStatement extends NodeStatement{
    public ValType returnType;
    public NodeExpression returnValue;

    public NodeReturnStatement(ValType returnType, NodeExpression returnValue) {
        this.returnType = returnType;
        this.returnValue = returnValue;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeReturnStatement(this, null);
    }
}