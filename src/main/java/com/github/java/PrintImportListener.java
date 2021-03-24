package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseListener;
import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

public class PrintImportListener extends JavaParserBaseListener {

    public String importPackageNamePrefix;

    public Set<String> importPackageNameSet = Sets.newHashSet();

    public String className;

    public PrintImportListener(String importPackageNamePrefix) {
        this.importPackageNamePrefix = importPackageNamePrefix;
    }

    @Override
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {

    }

    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
    }

    @Override
    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {

    }

    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        if (Objects.nonNull(ctx.qualifiedName())) {
            String qualifiedName = ctx.qualifiedName().getText();
            if (qualifiedName.contains(importPackageNamePrefix)) {
                importPackageNameSet.add(qualifiedName);
            }
        }
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
}
