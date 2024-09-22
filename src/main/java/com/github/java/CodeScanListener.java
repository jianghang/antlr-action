package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseListener;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.util.Objects;
import java.util.Set;

public class CodeScanListener extends JavaParserBaseListener {

    private Set<String> importNameSet;
    private BufferedTokenStream bufferedTokenStream;

    public CodeScanListener(Set<String> importNameSet, BufferedTokenStream bufferedTokenStream) {
        this.importNameSet = importNameSet;
        this.bufferedTokenStream = bufferedTokenStream;
    }

    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        if (Objects.nonNull(ctx.qualifiedName())) {
            if (importNameSet.contains(ctx.qualifiedName().getText())) {
                System.out.println(ctx.qualifiedName().getText());
            }
        }
    }
}
