package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseListener;

import java.util.Objects;

public class PrintAnnotationListener extends JavaParserBaseListener {

    private String annotationName;
    public String className;
    public boolean isHasTargetAnnotation;

    public PrintAnnotationListener(String annotationName) {
        this.annotationName = annotationName;
    }

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
    public void enterClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (Objects.nonNull(ctx.annotation())) {
            if (this.annotationName.equals(ctx.annotation().qualifiedName().getText())) {
                this.isHasTargetAnnotation = true;
            }
        }
    }
}
