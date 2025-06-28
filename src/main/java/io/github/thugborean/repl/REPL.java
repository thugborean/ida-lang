package io.github.thugborean.repl;

import java.util.List;
import java.util.Scanner;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.visitor.PrettyPrinterVisitor;
import io.github.thugborean.lexer.Lexer;
import io.github.thugborean.parser.Parser;
import io.github.thugborean.syntax.Token;

public class REPL {
    private static Scanner scanner;
    private static Lexer lexer;
    private static Parser parser;
    private static PrettyPrinterVisitor visitor;
    private static Program program;

    public static void run() {
        scanner = new Scanner(System.in);
        lexer = new Lexer();
        parser = new Parser();
        // List<Token> tokens = new ArrayList<>();
        while(true) {
            // Get the input line
            String input = scanner.nextLine();
            // Tokenize it
            List<Token> tokens = lexer.tokenize(input);
            // System.out.println(lexer.toString());
            parser.loadTokens(tokens);
            System.out.println("1");
            // Parse it and create AST
            program = parser.createAST();
            System.out.println("2");
            visitor = new PrettyPrinterVisitor();
            visitor.loadProgram(program);
            // Walk the AST and print it
            visitor.walkTree();
        }
    }
}