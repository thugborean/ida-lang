package io.github.thugborean.vm;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.lexer.Lexer;
import io.github.thugborean.parser.Parser;

public class VM {
    private Lexer lexer = new Lexer();
    private Parser parser;
    private Program program;

    // Top environment
    public final Environment environment = new Environment();

    public void execute(String source) {
        parser = new Parser(lexer.tokenize(source));
        program = parser.createAST();
        run(program);
    }

    public void run(Program program) {
        // Check for types

        // Implement logic
    }
}