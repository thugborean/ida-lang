package io.github.thugborean.ast.node;

import io.github.thugborean.ast.visitor.ASTVisitor;

public abstract interface NodeAST {
    <T> T accept(ASTVisitor<T> visitor);
}