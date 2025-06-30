package io.github.thugborean.repl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.github.thugborean.ast.node.Program;
import io.github.thugborean.ast.visitor.PrettyPrinterVisitor;
import io.github.thugborean.lexer.Lexer;
import io.github.thugborean.parser.Parser;
import io.github.thugborean.syntax.Token;

public class REPL {

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            Lexer lexer = new Lexer();
            Parser parser;
            PrettyPrinterVisitor visitor;
            String mode = "parser";

            while(true) {
                print("REPL Version: 0.1");
                print("Current Mode: " + mode);
                String input = scanner.nextLine();
                switch(input) {
                    case "::lexer":
                        mode = "lexer";
                        break;
                    case "::parser":
                        mode = "parser";
                        break;
                    case "::exit":
                        System.exit(0);
                    case "::help":
                        print(help());
                        break;
                    default: {
                        if(mode == "lexer") {
                            try {
                                List<Token> tokens = new ArrayList<>(lexer.tokenize(input));
                                for(Token token : tokens) print(token); // Print automatically calls toString()... I think
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                                break;
                            }
                        } else if(mode == "parser") {
                                try {
                                parser = new Parser(lexer.tokenize(input));
                                Program program = parser.createAST();
                                visitor = new PrettyPrinterVisitor(program);
                                visitor.walkTree();
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        }
    }
    private static void print(Object x) {
        System.out.println(x);
    }

    private String help() {
        StringBuilder str = new StringBuilder();
        str.append("::lexer - for lexer mode" + "\n");
        str.append("::parser - for parser mode" + "\n");
        str.append("::exit - to exit REPL" + "\n");
        return str.toString();
    }
}