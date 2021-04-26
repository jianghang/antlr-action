package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;

import java.util.Objects;

public class CheckDeclarationVisitor extends JavaParserBaseVisitor<Void> {

    public boolean isHasXmlWrapper;
    public boolean isBooleanType;

    @Override
    public Void visitClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (Objects.nonNull(ctx.annotation())) {
            if ("XmlElementWrapper".equals(ctx.annotation().qualifiedName().getText())) {
                this.isHasXmlWrapper = true;
            }
        }
        return null;
    }

    @Override
    public Void visitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        if (Objects.nonNull(ctx.typeType()) && Objects.nonNull(ctx.typeType().classOrInterfaceType())) {
            String type = ctx.typeType().classOrInterfaceType().getText();
            if ("Boolean".equals(type)) {
                this.isBooleanType = true;
            }
        }
        return null;
    }
}
