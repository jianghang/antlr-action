package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.stringtemplate.v4.ST;

import java.util.Objects;

public class ReplaceReturnStringVisitor extends JavaParserBaseVisitor<Void> {

    private static final String CLASS_TYPE = "class";
    private static final String INTERFACE_TYPE = "interface";

    private String type;
    private boolean isHasResponseBodyAnnotation;
    private boolean isHasOverrideAnnotation;
    public boolean isReplaceReturnString;
    private TokenStreamRewriter rewriter;

    public ReplaceReturnStringVisitor(TokenStreamRewriter rewriter, String type) {
        this.rewriter = rewriter;
        this.type = type;
    }

    @Override
    public Void visitClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (Objects.nonNull(ctx.annotation())) {
            String annotationStr = ctx.annotation().getText();
            if ("@Override".equals(annotationStr)) {
                this.isHasOverrideAnnotation = true;
            } else if ("@ResponseBody".equals(annotationStr)) {
                this.isHasResponseBodyAnnotation = true;
            }
        }
        return null;
    }

    @Override
    public Void visitTypeTypeOrVoid(JavaParser.TypeTypeOrVoidContext ctx) {
        if (Objects.nonNull(ctx.typeType()) && Objects.nonNull(ctx.typeType().classOrInterfaceType())) {
            String returnTypeName = ctx.typeType().classOrInterfaceType().IDENTIFIER(0).getText();
            boolean isReturnString = "String".equals(returnTypeName);
            if (CLASS_TYPE.equals(this.type) && isReturnString
                    && this.isHasOverrideAnnotation) {
                String returnShareResponse = "ShareResponse<String>";
                rewriter.replace(ctx.start, ctx.stop, returnShareResponse);
                this.isReplaceReturnString = true;
            } else if (INTERFACE_TYPE.equals(this.type) && isReturnString
                    && this.isHasResponseBodyAnnotation) {
                String returnShareResponse = "ShareResponse<String>";
                rewriter.replace(ctx.start, ctx.stop, returnShareResponse);
                this.isReplaceReturnString = true;
            }
        }
        return null;
    }

    @Override
    public Void visitStatement(JavaParser.StatementContext ctx) {
        if (CLASS_TYPE.equals(this.type) && this.isReplaceReturnString) {
            if (Objects.nonNull(ctx.RETURN())) {
                String returnExpression = ctx.expression(0).getText();
                String returnShareResponse = "ShareResponse.autoSuccess($returnExpression$)";
                ST st = new ST(returnShareResponse, '$', '$');
                st.add("returnExpression", returnExpression);
                returnShareResponse = st.render();
                rewriter.replace(ctx.expression(0).start, ctx.expression(0).stop, returnShareResponse);
            }
        }

        return null;
    }
}
