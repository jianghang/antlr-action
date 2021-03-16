package com.github.java8;

import com.github.java8.antlr.Java8Lexer;
import com.github.java8.antlr.Java8Parser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Java8Main {

    public static void main(String[] args) {
        String code = "package com.hangjiang.gen.tour;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "/**\n" +
                " * Created by jianghang on 2018/3/22.\n" +
                " */\n" +
                "public class Demo {\n" +
                "    void f(int x, String y) {\n" +
                "    }\n" +
                "\n" +
                "    ;\n" +
                "\n" +
                "    int[] g(/*no args*/) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    List<Map<String, Integer>>[] h() {\n" +
                "        return null;\n" +
                "    }\n" +
                "}";
        CharStream charStream = CharStreams.fromString(code);
        Java8Lexer java8Lexer = new Java8Lexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(java8Lexer);

        Java8Parser java8Parser = new Java8Parser(commonTokenStream);
        ParseTree parseTree = java8Parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        InsertSerialIdListener insertSerialIdListener = new InsertSerialIdListener(commonTokenStream);
        walker.walk(insertSerialIdListener, parseTree);
        System.out.println(insertSerialIdListener.rewriter.getText());
    }
}
