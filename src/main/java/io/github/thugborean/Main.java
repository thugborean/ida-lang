package io.github.thugborean;

import java.util.Scanner;

import io.github.thugborean.lexer.Lexer;
import io.github.thugborean.repl.REPL;

public class Main {
    private static String source;
    private static Scanner scanner;
    private static Lexer lexer; // Maybe bad because double define WIP
    public static void main(String[] args ) {

        // If given no arguments, run REPL
        // if (args[0].isEmpty()) {
            REPL.run();
    //     } else if(args[0] instanceof String) {
    //     // Set the source to the input file
    //         scanner = new Scanner(args[0]);
    //         source = scanner.toString();

    //         lexer = new Lexer();
    //         lexer.tokenize(source);
    //         lexer.toString();
    //     }
    // }

    }
}