package com.github.java;

import com.github.java.antlr.JavaLexer;
import com.github.java.antlr.JavaParser;
import com.google.common.collect.Sets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.UUID;

public class JavaMain {

    public static void main(String[] args) {
        String code = "package com.sunsharing.xshare.catalog.config;\n" +
                "\n" +
                "import com.alibaba.druid.pool.DruidDataSource;\n" +
                "import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;\n" +
                "import org.springframework.boot.context.properties.ConfigurationProperties;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "\n" +
                "import javax.sql.DataSource;\n" +
                "import java.sql.SQLException;\n" +
                "\n" +
                "@Configuration\n" +
                "public class DatabaseConfig {\n" +
                "\n" +
                "    @Bean\n" +
                "    @ConfigurationProperties(prefix = \"spring.datasource\")\n" +
                "    public DataSource dataSource() throws SQLException {\n" +
                "        DruidDataSource datasource = DruidDataSourceBuilder.create().build();\n" +
                "        datasource.setFilters(\"stat\");\n" +
                "        return datasource;\n" +
                "    }\n" +
                "}";
        CharStream charStream = CharStreams.fromString(code);
        JavaLexer javaLexer = new JavaLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(javaLexer);

        JavaParser javaParser = new JavaParser(commonTokenStream);
        ParseTree parseTree = javaParser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
//        InsertSerialIdListener insertSerialIdListener = new InsertSerialIdListener(commonTokenStream);
//        walker.walk(insertSerialIdListener, parseTree);
//        System.out.println(insertSerialIdListener.rewriter.getText());
//        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
        Set<String> importNameSet = Sets.newHashSet("com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder",
                "org.springframework.context.annotation.Bean");
        CodeScanListener codeScanListener = new CodeScanListener(importNameSet, commonTokenStream);
        walker.walk(codeScanListener, parseTree);
    }
}
