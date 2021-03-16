package com.github.java9;

import com.github.java9.antlr.Java9Lexer;
import com.github.java9.antlr.Java9Parser;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Java9Main {

    public static void main(String[] args) {
        String code = "package com.github.java8.example;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class Demo {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        List<String> stringList = new ArrayList<>();\n" +
                "    }\n" +
                "}\n";
        CharStream charStream = CharStreams.fromString(code);
        Java9Lexer java9Lexer = new Java9Lexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(java9Lexer);

        Java9Parser java9Parser = new Java9Parser(commonTokenStream);
        ParseTree parseTree = java9Parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        InsertSerialIdListener insertSerialIdListener = new InsertSerialIdListener(commonTokenStream);
        walker.walk(insertSerialIdListener, parseTree);
        System.out.println(insertSerialIdListener.rewriter.getText());
    }
}
