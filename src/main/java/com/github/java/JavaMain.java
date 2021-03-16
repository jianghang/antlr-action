package com.github.java;

import com.github.java.antlr.JavaLexer;
import com.github.java.antlr.JavaParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class JavaMain {

    public static void main(String[] args) {
        String code = "package com.github.java8.example;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public class Demo {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        List<String> stringList = new ArrayList<>();\n" +
                "        stringList.add(\"1\");\n" +
                "        List<Integer> integerList = stringList.stream().map(Integer::parseInt).collect(Collectors.toList());\n" +
                "        integerList.forEach(i -> {\n" +
                "            System.out.println(i);\n" +
                "        });\n" +
                "    }\n" +
                "}\n";
        CharStream charStream = CharStreams.fromString(code);
        JavaLexer javaLexer = new JavaLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(javaLexer);

        JavaParser javaParser = new JavaParser(commonTokenStream);
        ParseTree parseTree = javaParser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        InsertSerialIdListener insertSerialIdListener = new InsertSerialIdListener(commonTokenStream);
        walker.walk(insertSerialIdListener, parseTree);
        System.out.println(insertSerialIdListener.rewriter.getText());
    }
}
