package com.github.java8;

import com.github.java8.antlr.Java8Parser;
import com.github.java8.antlr.Java8ParserBaseListener;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

public class InsertSerialIdListener extends Java8ParserBaseListener {

    protected TokenStreamRewriter rewriter;

    public InsertSerialIdListener(TokenStream tokenStream) {
        rewriter = new TokenStreamRewriter(tokenStream);
    }

    @Override
    public void enterClassBody(Java8Parser.ClassBodyContext ctx) {
//        String field = "\n\tpublic static final long serialVersionUID = 1L;";
//        rewriter.insertAfter(ctx.start, field);
    }
}
