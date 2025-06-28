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

public class PrettyPrinterVisitor implements ASTVisitor {
    public int level = 0;
    private Program program;
    private StringBuilder buffer = new StringBuilder();

    public PrettyPrinterVisitor(Program program) {
        this.program = program;
    }

    // ??????
    public PrettyPrinterVisitor() {}

    public void walkTree() {
        // Go through all the nodes one by one and add their strings to the buffer
        for(NodeStatement node : program.nodes) {
            buffer.append(node.accept(this));
        }
        print();
    }

    @Override
    public String visitNodeBinaryExpression(NodeBinaryExpression node) {
        // Append what kind of operator
        String op = node.operator.lexeme;
        // Append lefthandside
        String lhs = node.leftHandSide.accept(this).toString();
        // Append righthandside
        String rhs = node.rightHandSide.accept(this).toString();
        // stringBuilder.append(node.rightHandSide.accept(this));
        return String.format("op: %-2s \n lhs: %-10s \n rhs: %-10s \n", op, lhs, rhs);
    }

    @Override
    public String visitUnaryExpression(NodeUnaryExpression node) {
        String op = node.operator.lexeme;
        String expr = node.expression.accept(this).toString();
        return String.format("op : %-2s \n expr: %-10s", op, expr);
    }

    @Override
    public String visitNodeNumericLiteral(NodeNumericLiteral node) {
        StringBuilder stringBuilder = new StringBuilder();
        // Append its value
        stringBuilder.append(node.token.literal);
        return stringBuilder.toString();
    }

    @Override
    public String visitNodeVariableReference(NodeVariableReference node) {
        StringBuilder stringBuilder = new StringBuilder();
        // Append its identifier
        stringBuilder.append(node.identifier);
        return stringBuilder.toString();
    }

    @Override
    public String visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Variable Declaration: " + "\n");
        // Append its type
        stringBuilder.append("\t" + "type: " + node.type.accept(this) + "\n");
        // Append its identifier
        stringBuilder.append("\t" + "identifier: " + node.identifier.lexeme + "\n");
        // Append its intial value
        stringBuilder.append("\t" + "value: \n");
        stringBuilder.append(node.initialValue.accept(this));
        return stringBuilder.toString();
    }

    @Override
    public String visitNodeType(NodeType node) {
        return node.type;
    }

    @Override
    public String visitNodePrintStatement(NodePrintStatement node) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Print Statement: \n");
        stringBuilder.append("printable: " + node.printable.accept(this));
        return stringBuilder.toString();
    }

    @Override
    public String visitExpressionStatement(NodeExpressionStatement node) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expression: ");
        stringBuilder.append(node.accept(this));
        return stringBuilder.toString();
    }

    @Override
    public String visitAssignStatement(NodeAssignStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignStatement'");
    }

    // Maybe not needed??? Will be removed in the future....
    @Override
    public String visitProgram(Program program) {
        program.accept(null);
        return "Program start: ";
    }

    public void print() {
        System.out.println(buffer.toString());
    }

    private void add(String str) {
        // Tab for each level
        for(int i = 0; i < level; i++) buffer.append("\t");
        buffer.append(str);
    }

    public void loadProgram(Program program) {
        this.program = program;
    }

    public void printSingleStatement(NodeExpression node) {
        System.out.println(node.accept(this));
    }
}