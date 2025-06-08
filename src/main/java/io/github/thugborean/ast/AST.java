package io.github.thugborean.ast;

import java.util.ArrayList;
import java.util.List;

import io.github.thugborean.syntax.Token;

public class AST {
    private Program program;

}

class Program {
    private List<Statement> statementList;
}

class variableDeclaration extends Statement {
    private List<Token> listOfTokens = new ArrayList<>();

}

abstract class Statement {

}