package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.Objects;

public class ReplaceVariableNameVisitor extends JavaParserBaseVisitor<Void> {

    private String sourceVarName;
    private String targetVarName;
    private TokenStreamRewriter rewriter;
    private String shareResponseVarName;

    public ReplaceVariableNameVisitor(TokenStreamRewriter rewriter, String sourceVarName, String targetVarName) {
        this.rewriter = rewriter;
        this.sourceVarName = sourceVarName;
        this.targetVarName = targetVarName;
    }

    @Override
    public Void visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        String varName = ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().IDENTIFIER().getText();
        if (this.sourceVarName.equals(varName)) {
            rewriter.insertBefore(ctx.start, "//");
        }
        visit(ctx.variableDeclarators());
        return null;
    }

    @Override
    public Void visitPrimary(JavaParser.PrimaryContext ctx) {
        if (Objects.nonNull(ctx.IDENTIFIER()) && sourceVarName.equals(ctx.IDENTIFIER().getText())) {
            rewriter.replace(ctx.start, ctx.stop, targetVarName);
        }
        return null;
    }
}
