package io.github.thugborean.ast.visitor;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.node.expression.NodeBinaryExpression;
import io.github.thugborean.ast.node.expression.NodeUnaryExpression;
import io.github.thugborean.ast.node.expression.NodeVariableReference;
import io.github.thugborean.ast.node.expression.literal.NodeNumericLiteral;
import io.github.thugborean.ast.node.statement.NodeAssignStatement;
import io.github.thugborean.ast.node.statement.NodeExpressionStatement;
import io.github.thugborean.ast.node.statement.NodePrintStatement;
import io.github.thugborean.ast.node.statement.NodeStatement;
import io.github.thugborean.ast.node.statement.NodeVariableDeclaration;
import io.github.thugborean.ast.node.types.NodeType;
import io.github.thugborean.syntax.TokenType;
import io.github.thugborean.vm.Environment;
import io.github.thugborean.vm.symbol.Value;
import io.github.thugborean.vm.symbol.Variable;

public class InterpreterVisitor implements ASTVisitor<Object>{
    private Environment environment;

    public InterpreterVisitor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void walkTree(Program program) {
        for(NodeStatement statement : program.nodes) {
            statement.accept(this);
        }
    }

    @Override
    public Value visitNodeNumericLiteral(NodeNumericLiteral node) {
        // Get the nodes value and store it in this wrapper
        return new Value(node.getValue());
    }

    @Override
    public Value visitNodeBinaryExpression(NodeBinaryExpression node) {
        Value left = (Value)node.leftHandSide.accept(this);
        Value right = (Value)node.rightHandSide.accept(this);
        // Determine the operator
        switch(node.operator.tokenType) {
            case TokenType.Plus: {
                // This may be faulty?
                return new Value(left.asNumber() + right.asNumber());
            }
            case TokenType.Minus: {
                // This may be faulty?
                return new Value(left.asNumber() - right.asNumber());
            }
            case TokenType.Multiply: {
                // This may be faulty?
                return new Value(left.asNumber() * right.asNumber());
            }
            case TokenType.Divide: {
                // This may be faulty?
                return new Value(left.asNumber() / right.asNumber());
            }
            case TokenType.Modulo: {
                // This may be faulty?
                return new Value(left.asNumber() % right.asNumber());
            }
            default: throw new RuntimeException("Interpreter Error: Couldn't find operator for expression!");
        }
    }

    @Override
    public Object visitUnaryExpression(NodeUnaryExpression node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpression'");
    }

    @Override
    public Value visitNodeVariableReference(NodeVariableReference node) {
        // Return the Value of the Variable
        Variable var = environment.getVariable(node.identifier);
        // Throw this error if we can't find the variable
        if (var == null) throw new RuntimeException("Undefined Symbol: " + node.identifier);
        return var.getvalue();
    }

    @Override
    public Object visitNodeVariableDeclaration(NodeVariableDeclaration node) {
        // Make a new variable with given identifier and evaluated epxression... type.type is not a banger...
        Variable var = new Variable(node.type.type, new Value(node.initialValue.accept(this)));
        environment.defineVariable(node.identifier.lexeme, var);
        // Nothing meaningful
        return null;
    }

    @Override
    public Object visitExpressionStatement(NodeExpressionStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitExpressionStatement'");
    }

    @Override
    public Object visitNodePrintStatement(NodePrintStatement node) {
        Value printable = (Value)node.printable.accept(this);
        // Print the pritable
        System.out.println(printable);
        // Nothing meaningful
        return null;
    }

    @Override
    public Object visitAssignStatement(NodeAssignStatement node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignStatement'");
    }

    @Override
    public String visitNodeType(NodeType node) {
        return node.type;
    }
    
}