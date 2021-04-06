package com.github.java;

import com.github.java.antlr.JavaLexer;
import com.github.java.antlr.JavaParser;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConvertAnnotationMain {

    private static final Set<String> excludeFileName = Sets.newHashSet("RestApiConfiguration.java",
            "CacheConfiguration.java",
            "ResponseConfiguration.java",
            "WebMvcConfiguration.java");

    public static void main(String[] args) throws IOException {
        //迁移xshare-management-common模块
        String sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-common\\src\\main\\java";
        String targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-common\\src\\main\\java";
//        testConvertApiAnnotation();
//        testConvertClassAnnotation();
        testPrintImport(sourceFolder, "com.sunsharing.xshare.framework.web.mvc.response.ShareResponseObject");
//        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-log-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log-api\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-log-api\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-log");

        //迁移xshare-management-api-log
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-log\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-log\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");
    }

    private static void testCodeMigrate(String sourceFolder, String targetFolder, String clientServerName) throws IOException {
        List<Path> sourcePathList = Lists.newArrayList();
//        getSingleFile(sourcePathList);
        getAllFile(Paths.get(sourceFolder), sourcePathList);
        handlerSourceFilePath2TargetPath(sourcePathList, sourceFolder, targetFolder, clientServerName);
    }

    private static void getSingleFile(List<Path> sourcePathList) {
        Path path = Paths.get("F:\\SunSharing_SourceCode\\xshare-management\\xshare-management-log\\src\\main\\java\\com\\sunsharing\\xshare\\management\\log\\controller\\FileXTestLogController.java");
        sourcePathList.add(path);
    }

    private static void testPrintImport(String sourceFolder, String packageName) throws IOException {
        List<Path> sourcePathList = Lists.newArrayList();
        getAllFile(Paths.get(sourceFolder), sourcePathList);
        Set<String> importPackageNameSet = Sets.newHashSet();
        for (Path path : sourcePathList) {
            String fileName = path.toString();
            if (!fileName.contains(".java")) {
                continue;
            }
            Multimap<String, String> multimap = printImport(path.toString(), packageName);
            for (String key : multimap.keySet()) {
                List<String> packageNameList = (List<String>) multimap.get(key);
                importPackageNameSet.addAll(packageNameList);
                packageNameList.forEach(name -> System.out.println(key + " : " + name));
            }
        }
        importPackageNameSet.forEach(System.out::println);
    }

    private static void testConvertClassAnnotation() {
        String code = "package com.sunsharing.xshare.management.server.controller.audit;\n" +
                "\n" +
                "import com.sunsharing.xshare.management.api.log.monitor.MetadataApi;\n" +
                "import com.sunsharing.xshare.management.request.ApplyColumnRequest;\n" +
                "import com.sunsharing.xshare.management.server.service.resource.databasex.MetadataService;\n" +
                "import com.sunsharing.xshare.management.vo.ApplyColumn;\n" +
                "\n" +
                "import org.springframework.stereotype.Controller;\n" +
                "import org.springframework.web.bind.annotation.PathVariable;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.ResponseBody;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "import javax.inject.Inject;\n" +
                "\n" +
                "/**\n" +
                " * @author dell\n" +
                " * @date 2020/4/14 17:02\n" +
                " * @version 1.0\n" +
                " */\n" +
                "@Controller\n" +
                "public class MetadataController implements MetadataApi {\n" +
                "    @Inject\n" +
                "    private MetadataService metadataService;\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @Override\n" +
                "    public String getDataRecordTableName(@PathVariable(\"schemeId\") String schemeId) {\n" +
                "        return metadataService.getDataRecordTableName(schemeId);\n" +
                "    }\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @Override\n" +
                "    public List<ApplyColumn> findApplyColumns(@RequestBody ApplyColumnRequest applyColumnParam) {\n" +
                "        return null;\n" +
                "    }\n" +
                "}";
        CommonTokenStream commonTokenStream = getTokenStream(code);
        CodeInfo codeInfo = convertClassAnnotation(commonTokenStream, "test-server");
        System.out.println(codeInfo.getCode());
    }

    private static void testConvertApiAnnotation() {
        String code = "package com.sunsharing.xshare.management.api.log.monitor;\n" +
                "\n" +
                "import com.sunsharing.xshare.framework.web.rest.RestApi;\n" +
                "import com.sunsharing.xshare.management.request.ApplyColumnRequest;\n" +
                "import com.sunsharing.xshare.management.vo.ApplyColumn;\n" +
                "\n" +
                "import org.springframework.web.bind.annotation.GetMapping;\n" +
                "import org.springframework.web.bind.annotation.PathVariable;\n" +
                "import org.springframework.web.bind.annotation.PostMapping;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.ResponseBody;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "/**\n" +
                " * @author dell\n" +
                " * @date 2020/4/14 16:59\n" +
                " * @version 1.0\n" +
                " */\n" +
                "@RestApi\n" +
                "public interface MetadataApi {\n" +
                "    @ResponseBody\n" +
                "    @GetMapping(\"/query/monitorComStatus/{schemeId}\")\n" +
                "    String getDataRecordTableName(@PathVariable(\"schemeId\") String schemeId);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @PostMapping(\"/find/ApplyColumns\")\n" +
                "    List<ApplyColumn> findApplyColumns(@RequestBody ApplyColumnRequest applyColumnParam);\n" +
                "}";
        CodeInfo codeInfo = convertApiAnnotation(code, "test-server");
        System.out.println(codeInfo.getCode());
    }

    private static void handlerSourceFilePath2TargetPath(List<Path> pathList, String sourceFolder, String targetFolder,
                                                         String clientServerName) throws IOException {
        for (Path sourcePath : pathList) {
            String targetPathStr = targetFolder + sourcePath.toString().replace(sourceFolder, "");
            Path targetPath = Paths.get(targetPathStr);
            if (excludeFileName.contains(targetPath.getFileName().toString())) {
                System.out.println("exclude path: " + targetPathStr);
                continue;
            }
            if (Files.exists(targetPath)) {
                System.out.println("exist path: " + targetPath);
                Files.delete(targetPath);
            }
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }
            System.out.println("parent: " + targetPath.getParent().toString());
            System.out.println("target: " + targetPath);
            if (targetPathStr.contains(".java")) {
                System.out.println("begin convert code...");
                CommonTokenStream commonTokenStream = getTokenStreamFromFile(sourcePath.toString());
                CodeInfo codeInfo = convertClassAnnotation(commonTokenStream, clientServerName);
                System.out.println("end convert code: " + codeInfo.getClassName());
                Files.write(targetPath, codeInfo.getCode().getBytes(Charsets.UTF_8));
            } else {
                Files.copy(sourcePath, targetPath);
            }
        }
    }

    private static void getAllFile(Path filePath, List<Path> filePathList) throws IOException {
        if (Files.isDirectory(filePath)) {
            List<Path> pathList = Files.list(filePath).collect(Collectors.toList());
            for (Path path : pathList) {
                getAllFile(path, filePathList);
            }
        } else {
            System.out.println("source: " + filePath.toString());
            filePathList.add(filePath);
        }
    }

    public static CodeInfo convertClassAnnotation(CommonTokenStream commonTokenStream, String clientServerName) {
        JavaParser javaParser = new JavaParser(commonTokenStream);
        ParseTree parseTree = javaParser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        ConvertAnnotationListener convertAnnotationListener = new ConvertAnnotationListener(commonTokenStream, clientServerName);
        walker.walk(convertAnnotationListener, parseTree);

        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName(convertAnnotationListener.packageName);
        codeInfo.setClassName(convertAnnotationListener.className);
        codeInfo.setCode(convertAnnotationListener.rewriter.getText());

        return codeInfo;
    }

    public static CodeInfo convertApiAnnotation(String code, String clientServerName) {
        CommonTokenStream commonTokenStream = getTokenStream(code);

        JavaParser javaParser = new JavaParser(commonTokenStream);
        ParseTree parseTree = javaParser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        ConvertApiAnnotationListener convertApiAnnotationListener = new ConvertApiAnnotationListener(commonTokenStream, clientServerName);
        walker.walk(convertApiAnnotationListener, parseTree);

        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName(convertApiAnnotationListener.packageName);
        codeInfo.setClassName(convertApiAnnotationListener.className);
        codeInfo.setCode(convertApiAnnotationListener.rewriter.getText());

        return codeInfo;
    }

    public static CommonTokenStream getTokenStream(String code) {
        CharStream charStream = CharStreams.fromString(code);
        JavaLexer javaLexer = new JavaLexer(charStream);
        return new CommonTokenStream(javaLexer);
    }

    public static CommonTokenStream getTokenStreamFromFile(String fileName) throws IOException {
        CharStream charStream = CharStreams.fromFileName(fileName);
        JavaLexer javaLexer = new JavaLexer(charStream);
        return new CommonTokenStream(javaLexer);
    }

    public static Multimap<String, String> printImport(String fileName, String importPackageNamePrefix) throws IOException {
        CommonTokenStream commonTokenStream = getTokenStreamFromFile(fileName);

        JavaParser javaParser = new JavaParser(commonTokenStream);
        ParseTree parseTree = javaParser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        PrintImportListener printImportListener = new PrintImportListener(importPackageNamePrefix);
        walker.walk(printImportListener, parseTree);

        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.putAll(printImportListener.className, printImportListener.importPackageNameSet);
        return multimap;
    }
}
