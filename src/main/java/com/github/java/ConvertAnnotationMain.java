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
//        String sourceFolder = "F:\\SunSharing_SourceCode\\xshare-management\\xshare-management-log\\src\\main\\java";
//        String targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-log\\src\\main\\java";

        String sourceFolder = "F:\\SunSharing_SourceCode\\xshare-management\\xshare-management-common\\src\\main\\java";
        String targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-common\\src\\main\\java";
//        testConvertApiAnnotation();
        testConvertClassAnnotation();
//        testPrintImport(sourceFolder, "com.sunsharing.xshare.framework");
//        testCodeMigrate(sourceFolder, targetFolder, "");
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
        String code = "package com.sunsharing.xshare.management.log.exception;\n" +
                "\n" +
                "import com.fasterxml.jackson.annotation.JsonInclude;\n" +
                "import com.fasterxml.jackson.databind.AnnotationIntrospector;\n" +
                "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                "import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;\n" +
                "import com.fasterxml.jackson.databind.type.TypeFactory;\n" +
                "import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;\n" +
                "import com.sunsharing.xshare.framework.core.exception.UncheckedException;\n" +
                "import com.sunsharing.xshare.framework.core.lang.StringUtils;\n" +
                "import com.sunsharing.xshare.framework.core.log.LoggerAdapter;\n" +
                "import com.sunsharing.xshare.framework.core.log.LoggerManager;\n" +
                "import com.sunsharing.xshare.framework.web.mvc.response.ShareResponseObject;\n" +
                "import com.sunsharing.xshare.framework.web.rest.exception.RestClientRequestException;\n" +
                "\n" +
                "import org.springframework.core.env.Environment;\n" +
                "import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;\n" +
                "import org.springframework.security.core.AuthenticationException;\n" +
                "import org.springframework.validation.ObjectError;\n" +
                "import org.springframework.web.bind.MethodArgumentNotValidException;\n" +
                "import org.springframework.web.multipart.MaxUploadSizeExceededException;\n" +
                "import org.springframework.web.servlet.HandlerExceptionResolver;\n" +
                "import org.springframework.web.servlet.ModelAndView;\n" +
                "import org.springframework.web.servlet.view.json.MappingJackson2JsonView;\n" +
                "\n" +
                "import java.text.SimpleDateFormat;\n" +
                "import java.util.List;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "import javax.inject.Inject;\n" +
                "import javax.servlet.http.HttpServletRequest;\n" +
                "import javax.servlet.http.HttpServletResponse;\n" +
                "\n" +
                "/**\n" +
                " * @author Administrator - 2018年04月2018/4/19日\n" +
                " */\n" +
                "public class ExceptionResolver implements HandlerExceptionResolver {\n" +
                "    private final LoggerAdapter logger = LoggerManager.getLogger(ExceptionResolver.class);\n" +
                "    @Inject\n" +
                "    Environment environment;\n" +
                "\n" +
                "    @Override\n" +
                "    public ModelAndView resolveException(HttpServletRequest request,\n" +
                "                                         HttpServletResponse response, Object handler, Exception ex) {\n" +
                "        logger.info(\"业务执行异常！\", ex);\n" +
                "\n" +
                "        ShareResponseObject shareResponseObject = new ShareResponseObject();\n" +
                "        shareResponseObject.setStatus(\"1500\");\n" +
                "        shareResponseObject.setMessage(\"访问异常，请联系管理员。\");\n" +
                "        shareResponseObject.setErrorMessage(ex.getMessage());\n" +
                "        if (ex instanceof MethodArgumentNotValidException) {\n" +
                "            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;\n" +
                "            shareResponseObject.setStatus(\"1800\");\n" +
                "            String defaultMessage = methodArgumentNotValidException.getBindingResult().getFieldError().getDefaultMessage();\n" +
                "            shareResponseObject.setMessage(\"数据校验异常。\" + defaultMessage);\n" +
                "            shareResponseObject.setDataView(defaultMessage);\n" +
                "        }\n" +
                "\n" +
                "        if (ex instanceof AuthorizationException) {\n" +
                "            shareResponseObject.setStatus(\"1201\");\n" +
                "            shareResponseObject.setMessage(\"授权异常\");\n" +
                "        } else if (ex instanceof FileException) {\n" +
                "            shareResponseObject.setStatus(\"1501\");\n" +
                "            shareResponseObject.setMessage(\"文件异常\");\n" +
                "        } else if (ex instanceof CheckDataException || ex instanceof com.sunsharing.xshare.management.common.exception.CheckDataException) {\n" +
                "            shareResponseObject.setStatus(\"1503\");\n" +
                "            shareResponseObject.setMessage(\"数据校验异常\");\n" +
                "        } else if (ex instanceof MaxUploadSizeExceededException) {\n" +
                "            shareResponseObject.setStatus(\"1505\");\n" +
                "            shareResponseObject.setMessage(\"文件超出大小限制\");\n" +
                "        } else if (ex instanceof UploadException) {\n" +
                "            shareResponseObject.setStatus(\"1201\");\n" +
                "            shareResponseObject.setMessage(\"文件上传异常\");\n" +
                "        } else if (ex instanceof UncheckedException) {\n" +
                "            shareResponseObject.setStatus(\"1506\");\n" +
                "            shareResponseObject.setMessage(\"运行时异常\");\n" +
                "        } else if (ex instanceof AuthenticationException) {\n" +
                "            // SPRING_SECURITY_LAST_EXCEPTION\n" +
                "            // 常见excption如下:\n" +
                "            // 用户名不存在:UsernameNotFoundException;\n" +
                "            // 密码错误:BadCredentialException;\n" +
                "            // 帐户被锁:LockedException;\n" +
                "            // 帐户未启动:DisabledException;\n" +
                "            // 密码过期:CredentialExpiredException;等等!\n" +
                "            shareResponseObject.setStatus(\"1404\");\n" +
                "            shareResponseObject.setMessage(\"用户名或密码输入不正确\");\n" +
                "        }\n" +
                "        if (ex instanceof RestClientRequestException) {\n" +
                "            RestClientRequestException restClientRequestException = (RestClientRequestException) ex;\n" +
                "            shareResponseObject.setStatus(restClientRequestException.getCode());\n" +
                "            if (\"1503\".equals(restClientRequestException.getCode())) {\n" +
                "\n" +
                "            } else if (\"1508\".equals(restClientRequestException.getCode())) {\n" +
                "                shareResponseObject.setMessage(\"数据库记录表名未配置，无法查询\");\n" +
                "            } else if (\"1505\".equals(restClientRequestException.getCode())) {\n" +
                "                shareResponseObject.setMessage(\"调用引擎异常\");\n" +
                "            } else if (\"1514\".equals(restClientRequestException.getCode())) {\n" +
                "                shareResponseObject.setErrorMessage(restClientRequestException.getMessage());\n" +
                "                shareResponseObject.setMessage(\"解析wsdl错误\");\n" +
                "            } else if (\"1515\".equals(restClientRequestException.getCode())) {\n" +
                "                shareResponseObject.setMessage(\"获取wsinfo错误\");\n" +
                "            } else if (\"1516\".equals(restClientRequestException.getCode())) {\n" +
                "                shareResponseObject.setMessage(\"调用引擎测试接口异常\");\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        if (hasJsonResopnse(request)) {\n" +
                "            MappingJackson2JsonView view = new MappingJackson2JsonView(createObjectMapper());\n" +
                "            view.setExtractValueFromSingleKeyModel(true);\n" +
                "            boolean showErrorMessage = getShowErrorMessageStatus();\n" +
                "            if (!showErrorMessage) {\n" +
                "                shareResponseObject.setErrorMessage(null);\n" +
                "            }\n" +
                "            return new ModelAndView(view, \"response\", shareResponseObject);\n" +
                "        }\n" +
                "        ModelAndView modelAndView = new ModelAndView();\n" +
                "        modelAndView.addObject(response);\n" +
                "        return modelAndView;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getShowErrorMessageStatus() {\n" +
                "        String property = environment.getProperty(\"xshare.response.errorMessage.show\");\n" +
                "        if (StringUtils.isNotEmpty(property)) {\n" +
                "            return Boolean.parseBoolean(property);\n" +
                "        }\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    private boolean hasJsonResopnse(HttpServletRequest request) {\n" +
                "        return headContainsContent(request, \"accept\", \"application/json\")\n" +
                "            || headContainsContent(request, \"accept\", \"*/*\")\n" +
                "            || headContainsContent(request, \"X-Requested-With\", \"XMLHttpRequest\");\n" +
                "    }\n" +
                "\n" +
                "    private boolean headContainsContent(HttpServletRequest request, String name, String content) {\n" +
                "        if (null == request.getHeader(name)) {\n" +
                "            return false;\n" +
                "        }\n" +
                "        if (request.getHeader(name).contains(content)) {\n" +
                "            return true;\n" +
                "        }\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    private ObjectMapper createObjectMapper() {\n" +
                "        AnnotationIntrospector primary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());\n" +
                "        AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();\n" +
                "        AnnotationIntrospector annotationIntrospector = AnnotationIntrospector.pair(primary, secondary);\n" +
                "        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();\n" +
                "        objectMapper.setAnnotationIntrospector(annotationIntrospector);\n" +
                "        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);\n" +
                "        objectMapper.setDateFormat(new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\"));\n" +
                "        return objectMapper;\n" +
                "    }\n" +
                "\n" +
                "    private String objectErrorToMessage(List<ObjectError> errors) {\n" +
                "        if (null == errors) {\n" +
                "            return \"\";\n" +
                "        }\n" +
                "        String errorMessage = errors.stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(\"，\"));\n" +
                "        return errorMessage + \"。\";\n" +
                "    }\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n";
        CommonTokenStream commonTokenStream = getTokenStream(code);
        CodeInfo codeInfo = convertClassAnnotation(commonTokenStream, "test-server");
        System.out.println(codeInfo.getCode());
    }

    private static void testConvertApiAnnotation() {
        String code = "package com.sunsharing.xshare.management.log.api.monitor;\n" +
                "\n" +
                "import com.sunsharing.xshare.framework.web.rest.RestApi;\n" +
                "import com.sunsharing.xshare.management.common.message.request.EsPageRequest;\n" +
                "import com.sunsharing.xshare.management.log.api.monitor.request.UpdateBusinessNumberRequest;\n" +
                "import com.sunsharing.xshare.management.log.api.monitor.view.DeTaskServiceView;\n" +
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
                " * @date 2020/3/26 11:31\n" +
                " * @version 1.0\n" +
                " */\n" +
                "//todo hxt\n" +
                "@RestApi\n" +
                "public interface DeTaskServiceApi {\n" +
                "    @ResponseBody\n" +
                "    @PostMapping(\"/query/deTaskServicesBy/requestIpLike\")\n" +
                "    List<DeTaskServiceView> queryDeTaskServicesByRequestIpLike(@RequestBody EsPageRequest<String> conditionPageRequest);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @GetMapping(\"/get/DeTaskServiceBy/{taskId}\")\n" +
                "    DeTaskServiceView getDeTaskServiceByTaskId(@PathVariable(\"taskId\") String taskId);\n" +
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
                continue;
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
