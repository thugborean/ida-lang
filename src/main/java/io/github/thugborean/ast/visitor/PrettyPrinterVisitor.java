package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeType;

public class PrettyPrinterVisitor implements ASTVisitor {

    @Override
    public Object visitNodeBinaryExpression(NodeBinaryExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBinaryExpression'");
    }

    @Override
    public Object visitNodeNumericLiteral(NodeNumericLiteral node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeNumericLiteral'");
    }

    @Override
    public Object visitNodeVariableReference(NodeVariableReference node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeVariableReference'");
    }

    @Override
    public Object visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeVariableDeclaration'");
    }

    @Override
    public Object visitNodeType(NodeType node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNodeType'");
    }

    @Override
    public Object visitProgram(Program program) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitProgram'");
    }
}