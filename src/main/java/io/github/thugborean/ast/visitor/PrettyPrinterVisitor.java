package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeUnaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.ast.node.statement.NodeAssignStatement;
import io.github.thugborean.ast.node.statement.NodeExpressionStatement;
import io.github.thugborean.ast.node.statement.NodePrintStatement;
import io.github.thugborean.ast.node.statement.NodeStatement;

public class PrettyPrinterVisitor implements ASTVisitor<Void> {
    private int indentLevel = 0;
    private StringBuilder output = new StringBuilder();

    public void walkTree(Program program) {
        // Go through all the nodes one by one and add their strings to the buffer
        for(NodeStatement node : program.nodes) {
            node.accept(this);
        }
    }

    @Override
    public Void visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        line("Variable Declaration:");
        indentLevel++;
        // For getting the type
        node.type.accept(this);
        line("identifier: " + node.identifier.lexeme);
        line("value: ");
        indentLevel++;
        node.initialValue.accept(this);
        indentLevel-=2;
        return null;
    }

    @Override
    public Void visitNodeBinaryExpression(NodeBinaryExpression node) {
        line("op: " + node.operator.lexeme);
        indentLevel++;

        line("lhs: ");
        indentLevel++;
        node.leftHandSide.accept(this);
        indentLevel--;

        line("rhs: ");
        indentLevel++;
        node.rightHandSide.accept(this);
        indentLevel--;

        indentLevel--;
        return null;
    }

    @Override
    public Void visitUnaryExpression(NodeUnaryExpression node) {
        line("op: " + node.operator.lexeme);
        indentLevel++;

        line("expression: ");
        indentLevel++;
        node.expression.accept(this);
        indentLevel--;

        indentLevel--;
        return null;
    }

    @Override
    public Void visitNodeNumericLiteral(NodeNumericLiteral node) {
        line(node.token.lexeme);
        return null;
    }

    @Override
    public Void visitNodeVariableReference(NodeVariableReference node) {
        line(node.identifier);
        return null;
    }

    // Not always needed
    public Void visitNodeType(NodeType node) {
        line("type: " + node.type);
        return null;
    }

    @Override
    public Void visitNodePrintStatement(NodePrintStatement node) {
        line("Print Statement: ");
        indentLevel++;
        line("printable: ");
        indentLevel++;
        node.printable.accept(this);
        indentLevel-=2;
        return null;
    }

    @Override
    public Void visitExpressionStatement(NodeExpressionStatement node) {
        node.value.accept(this);
        return null;
    }

    @Override
    public Void visitAssignStatement(NodeAssignStatement node) {
        line("Assign Statement: ");
        indentLevel++;

        line("reference: ");
        indentLevel++;
        node.variableReference.accept(this);
        indentLevel--;

        line("assigned: ");
        indentLevel++;
        node.assignedValue.accept(this);
        indentLevel--;

        indentLevel--;
        return null;
    }

    private void indent() {
        for(int i = 0; i < indentLevel; i++) output.append("    ");
    }

    private void line(String s) {
        indent();
        output.append(s).append("\n");
    }
    
    public void print() {
        System.out.println(output.toString());
        output.setLength(0);
    }

    // ?????
    public void printSingleStatement(NodeExpression node) {
        System.out.println(node.accept(this));
    }
}