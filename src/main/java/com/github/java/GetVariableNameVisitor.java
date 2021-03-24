package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class GetVariableNameVisitor extends JavaParserBaseVisitor<Void> {

    private String sourceClassName;
    private String targetClassName;
    public String sourceVarName;
    public String targetVarName;

    public GetVariableNameVisitor(String sourceClassName, String targetClassName) {
        this.sourceClassName = sourceClassName;
        this.targetClassName = targetClassName;
    }

    @Override
    public Void visitVariableInitializer(JavaParser.VariableInitializerContext ctx) {
        JavaParser.CreatorContext createdNameContext = ctx.expression().creator();
        if (Objects.nonNull(createdNameContext)) {
            String createdName = createdNameContext.createdName().getText();
            if (sourceClassName.equals(createdName)) {
                ParserRuleContext ruleContext = ctx.getParent();
                if (ruleContext instanceof JavaParser.VariableDeclaratorContext) {
                    JavaParser.VariableDeclaratorContext variableDeclaratorContext = (JavaParser.VariableDeclaratorContext) ruleContext;
                    if (StringUtils.isBlank(this.sourceVarName)) {
                        this.sourceVarName = variableDeclaratorContext.variableDeclaratorId().IDENTIFIER().getText();
                    }
                }
            } else if (targetClassName.equals(createdName)) {
                ParserRuleContext ruleContext = ctx.getParent();
                if (ruleContext instanceof JavaParser.VariableDeclaratorContext) {
                    JavaParser.VariableDeclaratorContext variableDeclaratorContext = (JavaParser.VariableDeclaratorContext) ruleContext;
                    if (StringUtils.isBlank(this.targetVarName)) {
                        this.targetVarName = variableDeclaratorContext.variableDeclaratorId().IDENTIFIER().getText();
                    }
                }
            }
        }

        return null;
    }
}
