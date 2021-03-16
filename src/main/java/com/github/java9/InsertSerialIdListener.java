package com.github.java9;

import com.github.java9.antlr.Java9BaseListener;
import com.github.java9.antlr.Java9Parser;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

public class InsertSerialIdListener extends Java9BaseListener {

    protected TokenStreamRewriter rewriter;

    public InsertSerialIdListener(TokenStream tokenStream) {
        rewriter = new TokenStreamRewriter(tokenStream);
    }

    @Override
    public void enterClassBody(Java9Parser.ClassBodyContext ctx) {
        String field = "\n\tpublic static final long serialVersionUID = 1L;";
        rewriter.insertAfter(ctx.start, field);
    }
}
