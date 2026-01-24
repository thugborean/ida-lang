package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.*;
import io.github.thugborean.ast.node.expression.literal.*;
import io.github.thugborean.ast.node.statement.*;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.vm.symbol.ValType;

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
        line(node.type.toString());
        line("identifier: " + node.identifier);
        line("value: ");
        indentLevel++;
        node.initializer.accept(this);
        indentLevel-=2;
        return null;
    }

    @Override
    public Void visitNodeBinaryExpression(NodeBinaryExpression node, ValType type) {
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
    public Void visitNodeUnaryExpression(NodeUnaryExpression node, ValType type) {
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
    public Void visitNodeExpressionStatement(NodeExpressionStatement node) {
        node.expression.accept(this);
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

    @Override
    public Void visitNodeNumericLiteral(NodeNumericLiteral node) {
        line(node.token.lexeme);
        return null;
    }

    @Override
    public Void visitNodeDoubleLiteral(NodeDoubleLiteral node) {
        line(node.token.lexeme);
        return null;
    }

    @Override
    public Void visitNodeStringLiteral(NodeStringLiteral node) {
        line(node.token.lexeme);
        return null;
    }

    @Override
    public Void visitNodeBooleanLiteral(NodeBooleanLiteral node) {
        line(node.token.lexeme);
        return null;
    }

    @Override
    public Void visitNodeNullLiteral(NodeNullLiteral node) {
        line(node.token.lexeme);
        return null;
    }

    @Override
    public Void visitNodeBlock(NodeBlock node) {
        for(NodeStatement statement : node.statements) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visitNodeFunctionDeclaration(NodeFunctionDeclaration node) {
        line("Function declaration: ");
        indentLevel++;
        line("Identifier: " + node.identifier);
        line("Return type: " + node.returnType);
        line("Parameters: " + node.parameters);
        line("Modifiers: " + node.modifiers);
        // line("Contents: " + node.contents.accept(this));
        indentLevel--;
        return null;
    }

    @Override
    public Void visitNodeReturnStatement(NodeReturnStatement node) {
        line("Return statement -> " + node.returnType);
        return null;
    }
}