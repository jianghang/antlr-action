package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseListener;
import com.google.common.collect.Sets;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConvertAnnotationListener extends JavaParserBaseListener {

    private static final String LOG_ADAPTER = "LoggerAdapter";

    public String packageName;

    public String className;

    private String clientServerName;

    protected TokenStreamRewriter rewriter;

    protected BufferedTokenStream bufferedTokenStream;

    private String loggerAdapterName;

    private boolean isHasLoggerAdapter;

    private boolean isNeedReplaceLogger;

    private boolean isHasController;

    private boolean isRestApi;

    private boolean isNeedReplaceJaxb;

    private String type;

    private Set<String> deleteImportPackageSet = Sets.newHashSet(
            "org.springframework.stereotype.Controller",
            "org.springframework.web.bind.annotation.ResponseBody",
            "com.sunsharing.xshare.framework.core.log.LoggerAdapter",
            "com.sunsharing.xshare.framework.core.log.LoggerManager",
            "com.sunsharing.xshare.framework.web.rest.RestApi",
            "org.springframework.web.bind.annotation.ResponseBody",
            "com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector");

    public ConvertAnnotationListener(BufferedTokenStream tokenStream) {
        rewriter = new TokenStreamRewriter(tokenStream);
        this.bufferedTokenStream = tokenStream;
    }

    public ConvertAnnotationListener(BufferedTokenStream tokenStream, String clientServerName) {
        rewriter = new TokenStreamRewriter(tokenStream);
        this.bufferedTokenStream = tokenStream;
        this.clientServerName = clientServerName;
    }

    @Override
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        if (isHasController) {
            String str = "\n\nimport org.springframework.web.bind.annotation.RestController;" +
                    "\nimport com.sunsharing.share.boot.framework.annotation.ShareRest;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (isHasLoggerAdapter) {
            String str = "\n\nimport lombok.extern.slf4j.Slf4j;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (isNeedReplaceLogger) {
            String str = "\n\nimport org.slf4j.Logger;\n" +
                    "import org.slf4j.LoggerFactory;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (isRestApi) {
            String str = "\n\nimport org.springframework.cloud.openfeign.FeignClient;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
    }

    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        packageName = ctx.qualifiedName().getText();
    }

    @Override
    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {

    }

    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        if (Objects.nonNull(ctx.qualifiedName())) {
            String qualifiedName = ctx.qualifiedName().getText();
            if (deleteImportPackageSet.contains(qualifiedName)) {
                rewriter.delete(ctx.start, ctx.stop);
                deleteImportLineBreak(ctx);
            }
            if ("com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector".equals(qualifiedName)) {
                this.isNeedReplaceJaxb = true;
            }
        }
    }

    private void deleteImportLineBreak(ParserRuleContext ctx) {
        Token token = ctx.getStop();
        int i = token.getTokenIndex();
        List<Token> tokenList = bufferedTokenStream.getHiddenTokensToRight(i);
        if (Objects.nonNull(tokenList)) {
            token = tokenList.get(0);
            if (Objects.nonNull(token)) {
                rewriter.replace(token, "");
            }
        }
    }

    @Override
    public void enterClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (Objects.nonNull(ctx.annotation())) {
            String annotation = ctx.annotation().getText();
            if ("@Controller".equals(annotation)) {
                String shareRestControllerStr = "@RestController\n" +
                        "@ShareRest";
                rewriter.replace(ctx.annotation().start, ctx.annotation().stop, shareRestControllerStr);
                this.isHasController = true;
            } else if ("@ResponseBody".equals(annotation)) {
                rewriter.delete(ctx.annotation().start, ctx.annotation().stop);
                deleteImportLineBreak(ctx.annotation());
            } else if ("@RestApi".equals(annotation)) {
                String feignClientStr = "@FeignClient(value = \"$clientServerName$\")";
                ST st = new ST(feignClientStr, '$', '$');
                st.add("clientServerName", clientServerName);
                feignClientStr = st.render();
                rewriter.replace(ctx.annotation().start, ctx.annotation().stop, feignClientStr);
                this.isRestApi = true;
            }
        }
    }

    @Override
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        if (Objects.nonNull(ctx.classDeclaration())) {
            this.className = ctx.classDeclaration().IDENTIFIER().getText();
            this.type = ctx.classDeclaration().CLASS().getText();
        } else if (Objects.nonNull(ctx.interfaceDeclaration())) {
            this.className = ctx.interfaceDeclaration().IDENTIFIER().getText();
            this.type = ctx.interfaceDeclaration().INTERFACE().getText();
        } else if (Objects.nonNull(ctx.enumDeclaration())) {
            this.className = ctx.enumDeclaration().IDENTIFIER().getText();
            this.type = ctx.enumDeclaration().ENUM().getText();
        }
    }

    @Override
    public void exitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        if (isHasLoggerAdapter) {
            rewriter.insertBefore(ctx.start, "@Slf4j\n");
        }
    }

    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        if (Objects.nonNull(ctx.typeType().classOrInterfaceType())) {
            String identifier = ctx.typeType().classOrInterfaceType().IDENTIFIER().get(0).getText();
            if (LOG_ADAPTER.equals(identifier)) {
                this.loggerAdapterName = ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getText();
                this.isHasLoggerAdapter = true;
                rewriter.delete(ctx.getParent().getParent().start, ctx.getParent().getParent().stop);
            }
        }
    }

    @Override
    public void enterConstDeclaration(JavaParser.ConstDeclarationContext ctx) {
        if (Objects.nonNull(ctx.typeType().classOrInterfaceType())) {
            String identifier = ctx.typeType().classOrInterfaceType().IDENTIFIER().get(0).getText();
            if (LOG_ADAPTER.equals(identifier)) {
                rewriter.replace(ctx.typeType().classOrInterfaceType().start, ctx.typeType().classOrInterfaceType().stop, "Logger");
                this.isNeedReplaceLogger = true;
            }
        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        if (this.isNeedReplaceJaxb) {
            GetVariableNameVisitor getVariableNameVisitor = new GetVariableNameVisitor(
                    "JaxbAnnotationIntrospector", "JacksonAnnotationIntrospector");
            ctx.accept(getVariableNameVisitor);
            if (StringUtils.isNotBlank(getVariableNameVisitor.sourceVarName)
                    && StringUtils.isNotBlank(getVariableNameVisitor.targetVarName)) {
                ReplaceVariableNameVisitor replaceVariableNameVisitor = new ReplaceVariableNameVisitor(this.rewriter,
                        getVariableNameVisitor.sourceVarName, getVariableNameVisitor.targetVarName);
                ctx.accept(replaceVariableNameVisitor);
            }
        }
    }

    @Override
    public void enterPrimary(JavaParser.PrimaryContext ctx) {
        if (Objects.nonNull(ctx.IDENTIFIER())) {
            if (isHasLoggerAdapter) {
                String identifier = ctx.IDENTIFIER().getText();
                if (this.loggerAdapterName.equals(identifier)) {
                    rewriter.replace(ctx.start, ctx.stop, "log");
                }
            }
            if (isNeedReplaceLogger) {
                String identifier = ctx.IDENTIFIER().getText();
                if ("LoggerManager".equals(identifier)) {
                    rewriter.replace(ctx.start, ctx.stop, "LoggerFactory");
                }
            }
        }
    }
}
