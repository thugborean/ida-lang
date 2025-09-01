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


    public void execute(String source) {
        parser = new Parser(lexer.tokenize(source));
        program = parser.createAST();
        run(program);
    }

    // This is the method that runs the logic
    // Each face will use its own symboltable and environments
    public void run(Program program) {
        // Check for types
        SymbolTable symbolTable = new SymbolTable();
        Environment topEnv = new Environment(null, symbolTable, 0);
        TypeCheckerVisitor typechecker = new TypeCheckerVisitor(topEnv);
        typechecker.walkTree(program);

        // Implement logic
        // Discard the typecheckers table and environment, because I can't be bothered
        topEnv = new Environment(null, symbolTable, 0);
        typechecker = new TypeCheckerVisitor(topEnv);
        InterpreterVisitor interpreter = new InterpreterVisitor(topEnv);
        interpreter.walkTree(program);
    }
}