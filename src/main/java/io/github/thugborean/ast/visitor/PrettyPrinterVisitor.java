package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeExpression;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeUnaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.ast.node.statement.NodeExpressionStatement;
import io.github.thugborean.ast.node.statement.NodePrintStatement;
import io.github.thugborean.ast.node.statement.NodeStatement;

public class PrettyPrinterVisitor implements ASTVisitor {
    public int level = 0;
    private Program program;
    StringBuilder buffer = new StringBuilder();

    public PrettyPrinterVisitor(Program program) {
        this.program = program;
    }

    // ??????
    public PrettyPrinterVisitor() {

    }

    public void walkTree() {
        // Go through all the nodes one by one and add their strings to the buffer
        if(!program.nodes.isEmpty()) System.out.println("2");
        for(NodeStatement node : program.nodes) {
            buffer.append(node.accept(this));
        }
        print();
    }

    @Override
    public String visitNodeBinaryExpression(NodeBinaryExpression node) {
        StringBuilder stringBuilder = new StringBuilder();
        // Append what kind of operator
        stringBuilder.append(node.operator.lexeme);
        // Append lefthandside
        stringBuilder.append(node.leftHandSide.accept(this));
        // Append righthandside
        stringBuilder.append(node.rightHandSide.accept(this));
        return stringBuilder.toString();
    }

    @Override
    public String visitUnaryExpression(NodeUnaryExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
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
        stringBuilder.append("Variable Declaration:");
        // Append its type
        stringBuilder.append("type: " + node.type.accept(this));
        // Append its identifier
        stringBuilder.append("identifier: " + node.identifier);
        // Append its intial value
        stringBuilder.append("value: " + node.initialValue.accept(this));
        return stringBuilder.toString();
    }

    @Override
    public String visitNodeType(NodeType node) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(node.type);
        return stringBuilder.toString();
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

    @Override
    public String visitNodePrintStatement(NodePrintStatement node) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Print Statement: \n");
        stringBuilder.append("printable: " + node.printable.accept(this));
        return stringBuilder.toString();
    }

    @Override
    public Object visitExpressionStatement(NodeExpressionStatement node) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expression: ");
        stringBuilder.append(node.accept(this));
        return stringBuilder.toString();
    }
}