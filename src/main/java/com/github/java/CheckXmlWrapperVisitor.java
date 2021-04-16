package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;

import java.util.Objects;

public class CheckXmlWrapperVisitor extends JavaParserBaseVisitor<Void> {

    public boolean isHasXmlWrapper;

    @Override
    public Void visitClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (Objects.nonNull(ctx.annotation())) {
            if ("XmlElementWrapper".equals(ctx.annotation().qualifiedName().getText())) {
                this.isHasXmlWrapper = true;
            }
        }
        return null;
    }
}
