package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseListener;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.stringtemplate.v4.ST;

import java.util.Objects;

public class ConvertApiAnnotationListener extends JavaParserBaseListener {

    public String packageName;

    public String className;

    private String clientServerName;

    protected TokenStreamRewriter rewriter;

    public ConvertApiAnnotationListener(TokenStream tokenStream, String clientServerName) {
        rewriter = new TokenStreamRewriter(tokenStream);
        this.clientServerName = clientServerName;
    }

    @Override
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {

    }

    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        packageName = ctx.qualifiedName().getText();
        String str = "\n\nimport org.springframework.cloud.openfeign.FeignClient;";
        rewriter.insertAfter(ctx.stop, str);
    }

    @Override
    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {

    }

    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        if (Objects.nonNull(ctx.qualifiedName())) {
            String qualifiedName = ctx.qualifiedName().getText();
            if ("com.sunsharing.xshare.framework.web.rest.RestApi".equals(qualifiedName)) {
                rewriter.replace(ctx.start, ctx.stop, "");
            } else if ("org.springframework.web.bind.annotation.ResponseBody".equals(qualifiedName)) {
                rewriter.replace(ctx.start, ctx.stop, "");
            }
        }
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        className = ctx.IDENTIFIER().getText();
    }

    @Override
    public void enterClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (Objects.nonNull(ctx.annotation())) {
            String annotation = ctx.annotation().getText();
            if ("@RestApi".equals(annotation)) {
                String feignClientStr = "@FeignClient(value = \"$clientServerName$\")";
                ST st = new ST(feignClientStr, '$', '$');
                st.add("clientServerName", clientServerName);
                feignClientStr = st.render();
                rewriter.replace(ctx.annotation().start, ctx.annotation().stop, feignClientStr);
            } else if ("@ResponseBody".equals(annotation)) {
                rewriter.replace(ctx.annotation().start, ctx.annotation().stop, "");
            }
        }
    }
}
