package com.github.java;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.List;
import java.util.Objects;

public class ParserUtils {

    public static void deleteImportLineBreak(ParserRuleContext ctx, BufferedTokenStream bufferedTokenStream, TokenStreamRewriter rewriter) {
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

    public static <T> ParserRuleContext getParent(ParserRuleContext parserRuleContext,Class<T> cls) {
        do {
            parserRuleContext = parserRuleContext.getParent();
            if(Objects.isNull(parserRuleContext)){
                return null;
            }
        } while (!parserRuleContext.getClass().isAssignableFrom(cls));
        return parserRuleContext;
    }
}
