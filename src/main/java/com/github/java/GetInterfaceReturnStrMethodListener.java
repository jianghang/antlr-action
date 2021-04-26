package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseListener;
import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

public class GetInterfaceReturnStrMethodListener extends JavaParserBaseListener {

    public String packageName;
    public String className;
    public Set<String> methodSet = Sets.newHashSet();

    @Override
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        if (Objects.nonNull(ctx.classDeclaration())) {
            this.className = ctx.classDeclaration().IDENTIFIER().getText();
        } else if (Objects.nonNull(ctx.interfaceDeclaration())) {
            this.className = ctx.interfaceDeclaration().IDENTIFIER().getText();
        } else if (Objects.nonNull(ctx.enumDeclaration())) {
            this.className = ctx.enumDeclaration().IDENTIFIER().getText();
        }
    }

    @Override
    public void enterInterfaceMethodDeclaration(JavaParser.InterfaceMethodDeclarationContext ctx) {
        if (Objects.nonNull(ctx.typeTypeOrVoid().typeType()) &&
                Objects.nonNull(ctx.typeTypeOrVoid().typeType().classOrInterfaceType())) {
            String returnType = ctx.typeTypeOrVoid().typeType().classOrInterfaceType().getText();
            if ("String".equals(returnType)) {
                String methodName = ctx.IDENTIFIER().getText();
                methodSet.add(methodName);
            }
        }
    }
}
