package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseVisitor;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.stringtemplate.v4.ST;

import java.util.Objects;

public class ReplaceXmlAnnotationVisitor extends JavaParserBaseVisitor<Void> {

    public boolean isHasXmlWrapper;
    private TokenStreamRewriter rewriter;
    private BufferedTokenStream bufferedTokenStream;

    public ReplaceXmlAnnotationVisitor() {

    }

    public ReplaceXmlAnnotationVisitor(TokenStreamRewriter rewriter, BufferedTokenStream bufferedTokenStream, boolean isHasXmlWrapper) {
        this.rewriter = rewriter;
        this.bufferedTokenStream = bufferedTokenStream;
        this.isHasXmlWrapper = isHasXmlWrapper;
    }

    @Override
    public Void visitClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (Objects.nonNull(ctx.annotation())) {
            if ("XmlElement".equals(ctx.annotation().qualifiedName().getText())) {
                if (this.isHasXmlWrapper) {
                    rewriter.delete(ctx.annotation().start, ctx.annotation().stop);
                    ParserUtils.deleteImportLineBreak(ctx.annotation(), this.bufferedTokenStream, this.rewriter);
                } else {
                    replaceJsonProperty(ctx);
                }
            } else if ("XmlElementWrapper".equals(ctx.annotation().qualifiedName().getText())) {
                if (this.isHasXmlWrapper) {
                    replaceJsonProperty(ctx);
                }
            }
        }

        return null;
    }

    private void replaceJsonProperty(JavaParser.ClassOrInterfaceModifierContext ctx) {
        String jsonProperty = "@JsonProperty(value = $value$)";
        ST st = new ST(jsonProperty, '$', '$');
        if (Objects.nonNull(ctx.annotation().elementValuePairs())) {
            String value = ctx.annotation().elementValuePairs().elementValuePair(0).elementValue().expression().primary().literal().STRING_LITERAL().getText();
            st.add("value", value);
            rewriter.replace(ctx.annotation().start, ctx.annotation().stop, st.render());
        } else {
            rewriter.delete(ctx.annotation().start, ctx.annotation().stop);
            ParserUtils.deleteImportLineBreak(ctx.annotation(), this.bufferedTokenStream, this.rewriter);
        }
    }
}
