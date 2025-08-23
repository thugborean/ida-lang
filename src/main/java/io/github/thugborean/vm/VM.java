package io.github.thugborean.vm;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.visitor.InterpreterVisitor;
import io.github.thugborean.ast.visitor.TypeCheckerVisitor;
import io.github.thugborean.lexer.Lexer;
import io.github.thugborean.parser.Parser;
import io.github.thugborean.vm.symbol.SymbolTable;

public class VM {
    private Lexer lexer = new Lexer();
    private Parser parser;
    private Program program;
    private final SymbolTable symbolTable = new SymbolTable();

    // Top environment, level 0
    public final Environment environment = new Environment(null, symbolTable, 0);

    public void execute(String source) {
        parser = new Parser(lexer.tokenize(source));
        program = parser.createAST();
        run(program);
    }

    // This is the method that runs the logic
    public void run(Program program) {
        // Check for types
        TypeCheckerVisitor typechecker = new TypeCheckerVisitor();
        typechecker.walkTree(program);
        // Implement logic
        InterpreterVisitor interpreter = new InterpreterVisitor(environment);
        interpreter.walkTree(program);
    }
}