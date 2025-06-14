package io.github.thugborean.repl;

import java.util.Scanner;

import io.github.thugborean.lexer.Lexer;

public class REPL {
    private static Scanner scanner;
    private static Lexer lexer;

    public static void run() {
        scanner = new Scanner(System.in);
        lexer = new Lexer();
        // List<Token> tokens = new ArrayList<>();
        while(true) {
            String input = scanner.nextLine();
            lexer.tokenize(input);
            System.out.println(lexer.toString());
        }
    }
}
