package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.stringtemplate.v4.ST;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ReplaceReturnStringVisitor extends JavaParserBaseVisitor<Void> {

    private static final String CLASS_TYPE = "class";
    private static final String INTERFACE_TYPE = "interface";

    private String type;
    private boolean isHasResponseBodyAnnotation;
    private boolean isHasOverrideAnnotation;
    public boolean isReplaceReturnString;
    private TokenStreamRewriter rewriter;
    private BufferedTokenStream bufferedTokenStream;
    private Set<String> needImportPackage;
    private Map<String, String> classAliasMap;

    public ReplaceReturnStringVisitor(TokenStreamRewriter rewriter, BufferedTokenStream bufferedTokenStream, String type,
                                      Set<String> needImportPackage, Map<String, String> classAliasMap) {
        this.rewriter = rewriter;
        this.bufferedTokenStream = bufferedTokenStream;
        this.type = type;
        this.needImportPackage = needImportPackage;
        this.classAliasMap = classAliasMap;
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
                needImportPackage.add("\nimport com.sunsharing.share.boot.framework.web.standard.entity.ShareResponse;");
                this.isReplaceReturnString = true;
            } else if (INTERFACE_TYPE.equals(this.type) && isReturnString
                    && this.isHasResponseBodyAnnotation) {
                String returnShareResponse = "ShareResponse<String>";
                rewriter.replace(ctx.start, ctx.stop, returnShareResponse);
                needImportPackage.add("\nimport com.sunsharing.share.boot.framework.web.standard.entity.ShareResponse;");
                this.isReplaceReturnString = true;
            } else if (CLASS_TYPE.equals(this.type) && isReturnString
                    && this.isHasResponseBodyAnnotation) {
                String returnShareResponse = "ShareResponse<String>";
                rewriter.replace(ctx.start, ctx.stop, returnShareResponse);
                needImportPackage.add("\nimport com.sunsharing.share.boot.framework.web.standard.entity.ShareResponse;");
                this.isReplaceReturnString = true;
            }
        }
        return null;
    }

    @Override
    public Void visitStatement(JavaParser.StatementContext ctx) {
        if (CLASS_TYPE.equals(this.type) && this.isReplaceReturnString) {
            boolean isNeedAutoSuccess = true;
            if (Objects.nonNull(ctx.RETURN())) {
                if (Objects.nonNull(ctx.expression(0).expression()) && ctx.expression(0).expression().size() > 0
                        && Objects.nonNull(ctx.expression(0).expression(0).primary())
                        && Objects.nonNull(ctx.expression(0).methodCall())) {
                    String varName = ctx.expression(0).expression(0).primary().getText();
                    String methodName = ctx.expression(0).methodCall().IDENTIFIER().getText();
                    String className = this.classAliasMap.get(varName);
                    if (Objects.nonNull(className) && CommonConstant.methodMap.containsKey(className)
                            && CommonConstant.methodMap.get(className).contains(methodName)) {
                        isNeedAutoSuccess = false;
                    }
                }
                if (isNeedAutoSuccess) {
                    String returnExpression = bufferedTokenStream.getText(ctx.expression(0));
                    String returnShareResponse = "ShareResponse.autoSuccess($returnExpression$)";
                    ST st = new ST(returnShareResponse, '$', '$');
                    st.add("returnExpression", returnExpression);
                    returnShareResponse = st.render();
                    rewriter.replace(ctx.expression(0).start, ctx.expression(0).stop, returnShareResponse);
                }
            }
        }

        if (Objects.nonNull(ctx.statement()) && ctx.statement().size() > 0) {
            for (JavaParser.StatementContext statementContext : ctx.statement()) {
                visit(statementContext);
            }
        }
        if (Objects.nonNull(ctx.block()) && Objects.nonNull(ctx.block().blockStatement())
                && ctx.block().blockStatement().size() > 0) {
            for (JavaParser.BlockStatementContext blockStatementContext : ctx.block().blockStatement()) {
                visit(blockStatementContext);
            }
        }

        return null;
    }
}
