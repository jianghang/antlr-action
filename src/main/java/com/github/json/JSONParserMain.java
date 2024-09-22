package com.github.json;

import com.github.json.antlr.JSONLexer;
import com.github.json.antlr.JSONParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class JSONParserMain {
    public static void main(String[] args) {
        String json = "{\n" +
                "  \"glossary\": {\n" +
                "    \"title\": \"example glossary\",\n" +
                "    \"GlossDiv\": {\n" +
                "      \"title\": \"S\",\n" +
                "      \"GlossList\": {\n" +
                "        \"GlossEntry\": {\n" +
                "          \"ID\": \"SGML\",\n" +
                "          \"SortAs\": \"SGML\",\n" +
                "          \"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                "          \"Acronym\": \"SGML\",\n" +
                "          \"Abbrev\": \"ISO 8879:1986\",\n" +
                "          \"GlossDef\": {\n" +
                "            \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
                "            \"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
                "          },\n" +
                "          \"GlossSee\": \"markup\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        System.out.println(json);
        CharStream charStream = CharStreams.fromString(json);
        JSONLexer jsonLexer = new JSONLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(jsonLexer);

        JSONParser jsonParser = new JSONParser(commonTokenStream);
        ParseTree parseTree = jsonParser.json();

        ParseTreeWalker walker = new ParseTreeWalker();
        ConvertJsonToDrlListener convertJsonToDrlListener = new ConvertJsonToDrlListener();
        walker.walk(convertJsonToDrlListener, parseTree);
    }
}
