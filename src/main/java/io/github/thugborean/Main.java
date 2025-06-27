package io.github.thugborean;

import java.util.Scanner;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.visitor.PrettyPrinterVisitor;
import io.github.thugborean.lexer.Lexer;
import io.github.thugborean.parser.Parser;
import io.github.thugborean.repl.REPL;

public class Main {
    public static void main(String[] args ) {
            // REPL.run();
            String source = "num a = 1 + 2 * 3;";
            Lexer lexer = new Lexer();
            Parser parser = new Parser(lexer.tokenize(source));
            Program program = parser.createAST();
            PrettyPrinterVisitor visitor = new PrettyPrinterVisitor(program);
            visitor.walkTree();            
    }
}