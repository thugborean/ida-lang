package io.github.thugborean.ast.node.statement;

import java.util.Set;
import io.github.thugborean.vm.symbol.Param;
import io.github.thugborean.ast.visitor.ASTVisitor;
import io.github.thugborean.vm.symbol.Modifier;
import io.github.thugborean.vm.symbol.ValType;

public class NodeFunctionDeclaration extends NodeStatement{
    public String identifier;
    public ValType returnType;
    
    public Set<Param> parameters;
    public Set<Modifier> modifiers;
    public NodeBlock contents;

    public NodeFunctionDeclaration(String identifier, ValType returnType, Set<Param> paramaters, Set<Modifier> modifiers, NodeBlock contents) {
        this.identifier = identifier;
        this.returnType = returnType;
        this.parameters = paramaters;
        this.modifiers = modifiers;
        this.contents = contents;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeFunctionDeclaration(this);
    }
}
