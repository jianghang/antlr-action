package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;

public class ReplaceObjectResponseVisitor extends JavaParserBaseVisitor<Void> {

    private String returnParamType;
    private String returnParamVarName;
    private Set<String> needImportPackageSet;

    private TokenStreamRewriter rewriter;

    public ReplaceObjectResponseVisitor(TokenStreamRewriter rewriter, Set<String> needImportPackageSet) {
        this.rewriter = rewriter;
        this.needImportPackageSet = needImportPackageSet;
    }

    @Override
    public Void visitTypeTypeOrVoid(JavaParser.TypeTypeOrVoidContext ctx) {
        if (Objects.nonNull(ctx.typeType())
                && Objects.nonNull(ctx.typeType().classOrInterfaceType())
                && Objects.nonNull(ctx.typeType().classOrInterfaceType().IDENTIFIER())
                && ctx.typeType().classOrInterfaceType().IDENTIFIER().size() > 0
                && Objects.nonNull(ctx.typeType().classOrInterfaceType().typeArguments())
                && ctx.typeType().classOrInterfaceType().typeArguments().size() > 0) {
            String returnType = ctx.typeType().classOrInterfaceType().IDENTIFIER().get(0).getText();
            if ("ObjectResponse".equals(returnType)) {
                this.returnParamType = ctx.typeType().classOrInterfaceType().typeArguments()
                        .get(0).typeArgument(0).typeType().classOrInterfaceType().getText();
                rewriter.replace(ctx.typeType().classOrInterfaceType().start, ctx.typeType().classOrInterfaceType().stop, this.returnParamType);
            }
            if ("ListResponse".equals(returnType)) {
                this.returnParamType = ctx.typeType().classOrInterfaceType().typeArguments()
                        .get(0).typeArgument(0).typeType().classOrInterfaceType().getText();
                this.returnParamType = "List<" + this.returnParamType + ">";
                rewriter.replace(ctx.typeType().classOrInterfaceType().start, ctx.typeType().classOrInterfaceType().stop, this.returnParamType);
            }
        }
        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        if (StringUtils.isNotBlank(this.returnParamType)
                && Objects.nonNull(ctx.typeType().classOrInterfaceType())
                && Objects.nonNull(ctx.variableDeclarators().variableDeclarator())
                && ctx.variableDeclarators().variableDeclarator().size() > 0) {
            if (this.returnParamType.equals(ctx.typeType().classOrInterfaceType().getText())) {
                this.returnParamVarName = ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getText();
            }
        }

        return null;
    }

    @Override
    public Void visitStatement(JavaParser.StatementContext ctx) {
        if (Objects.nonNull(ctx.RETURN()) && Objects.nonNull(ctx.expression())
                && StringUtils.isNotBlank(this.returnParamVarName)) {
            rewriter.replace(ctx.expression(0).start, ctx.expression(0).stop, returnParamVarName);
        }
        return null;
    }
}
