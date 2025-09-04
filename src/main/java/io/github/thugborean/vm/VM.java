package io.github.thugborean.vm;

import java.util.ArrayDeque;
import java.util.Deque;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.visitor.InterpreterVisitor;
import io.github.thugborean.ast.visitor.TypeCheckerVisitor;
import io.github.thugborean.lexer.Lexer;
import io.github.thugborean.parser.Parser;

public class VM {
    private Lexer lexer = new Lexer();
    private Parser parser;
    private Program program;
    public Deque<Environment> envStack = new ArrayDeque<>();

    public void execute(String source) {
        parser = new Parser(lexer.tokenize(source));
        program = parser.createAST();
        run(program);
    }

    // This is the method that runs the logic
    // Each face will use its own symboltable and environments
    public void run(Program program) {
        // Typechecking
        Environment topEnv = new Environment();
        envStack.addLast(topEnv);
        TypeCheckerVisitor typechecker = new TypeCheckerVisitor(topEnv, this);
        typechecker.walkTree(program);

        envStack.clear();
        
        // Implement logic
        topEnv = new Environment();
        envStack.addLast(topEnv);
        InterpreterVisitor interpreter = new InterpreterVisitor(this);
        interpreter.walkTree(program);

        envStack.clear();
    }

    public void enterScope() {
        Environment newEnv = new Environment(getCurrentEnv());
        envStack.addLast(newEnv);
    }

    public void exitScope() {
        getCurrentEnv().close();
        envStack.removeLast();
    }

    public Environment getCurrentEnv() {
        return envStack.peekLast();
    }
}