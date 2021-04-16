package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.stringtemplate.v4.ST;

import java.util.Objects;

public class ReplaceMethodCallVisitor extends JavaParserBaseVisitor<Void> {

    private TokenStreamRewriter rewriter;
    private String shareResponseVarName;
    private String shareTestResponseVarName;
    public boolean isNeedImportShareResponse;

    public ReplaceMethodCallVisitor(TokenStreamRewriter rewriter) {
        this.rewriter = rewriter;
    }

    @Override
    public Void visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        String varName = ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().IDENTIFIER().getText();
        if (Objects.nonNull(ctx.typeType().classOrInterfaceType())) {
            String typeName = ctx.typeType().classOrInterfaceType().IDENTIFIER().get(0).getText();
            if ("ShareResponseObject".equals(typeName)) {
                this.shareResponseVarName = varName;
                String shareResponseStr = "ShareResponse $shareResponseObject$ = new ShareResponse();";
                ST st = new ST(shareResponseStr, '$', '$');
                st.add("shareResponseObject", varName);
                rewriter.replace(ctx.start, ctx.stop, st.render());
                this.isNeedImportShareResponse = true;
            }
            if ("ShareTestResponseObject".equals(typeName)) {
                this.shareTestResponseVarName = varName;
            }
        }
        visit(ctx.variableDeclarators());
        return null;
    }

    @Override
    public Void visitExpression(JavaParser.ExpressionContext ctx) {
        if (Objects.nonNull(ctx.expression()) && ctx.expression().size() > 0
                && Objects.nonNull(ctx.expression(0).primary())
                && Objects.nonNull(ctx.expression(0).primary().IDENTIFIER())) {
            String expressionVarName = ctx.expression(0).primary().IDENTIFIER().getText();
            if (Objects.nonNull(this.shareResponseVarName) &&
                    this.shareResponseVarName.equals(expressionVarName)
                    && Objects.nonNull(ctx.methodCall())) {
                String methodCall = ctx.methodCall().IDENTIFIER().getText();
                if ("setErrorMessage".equals(methodCall)) {
                    rewriter.replace(ctx.methodCall().IDENTIFIER().getSymbol(), "setErrorDetail");
                }
            }
            if (Objects.nonNull(this.shareTestResponseVarName) &&
                    this.shareTestResponseVarName.equals(expressionVarName)
                    && Objects.nonNull(ctx.methodCall())) {
                String methodCall = ctx.methodCall().IDENTIFIER().getText();
                if ("setDataView".equals(methodCall)) {
                    rewriter.replace(ctx.methodCall().IDENTIFIER().getSymbol(), "setData");
                }
            }
        }
        return null;
    }
}
