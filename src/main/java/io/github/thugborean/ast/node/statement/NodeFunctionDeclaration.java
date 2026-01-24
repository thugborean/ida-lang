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

    // Returns false if the last statement isn't a return-statement if the return type is anything other than void
    // Adds the contents of the function to it's AST
    public boolean addContents(NodeBlock contents) {
        if(returnType != ValType.VOID) {
            NodeStatement last = contents.statements.getLast();
            if(!(last instanceof NodeReturnStatement)) return false;
                else {
                    this.contents = contents;
                }
        }
        this.contents = contents;
        return true;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNodeFunctionDeclaration(this);
    }
}
