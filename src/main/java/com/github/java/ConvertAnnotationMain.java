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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConvertAnnotationMain {

    private static final Set<String> excludeFileName = Sets.newHashSet(
            "CacheConfiguration.java",
            "RocketMqConfiguration.java",
            "ConfigService.java",
            "DatabaseConfiguration.java",
            "DatabasePropertyConfig.java",
            "ElasticsearchConfiguration.java",
            "FreeMarkerConfiguration.java",
            "RedisConfiguration.java",
            "RequestFilter.java",
            "ResponseConfiguration.java",
            "RestApiConfiguration.java",
            "RocketMqConfiguration.java",
            "SysConfig.java",
            "WebConfiguration.java",
            "WebMvcConfiguration.java",
            "LogWriteDatabaseTypeCondition.java",
            "LogReadDatabaseTypeCondition.java",
            "SqlInjectionAspect.java",
            "ScurdConfiguration.java",
            "ExtensionCasAuthenticationEntryPoint.java",
            "MyRedirectStrategy.java",
            "WebSecurityConfiguration.java",
            "SystemConstant.java",
            "CorsFilter.java",
            "CasConfig.java",
            "HttpSessionConfigListener.java",
            "MyCasAuthenticationEntryPoint.java",
            "AuthController.java",
            "HttpProxyServlet.java",
            "SpringUtil.java",
            "XxlJobConfig.java",
            "ItextService.java",
            "Test2Controller.java");
    private static final Set<String> excludeFilePath = Sets.newHashSet(
            "com\\sunsharing\\xshare\\management\\log\\exception\\ExceptionResolver.java"
            , "com\\sunsharing\\xshare\\management\\server\\service\\catalog\\CatalogScheduledService.java"
            , "com\\sunsharing\\xshare\\management\\server\\service\\statistics\\DataSurveyScheduledService.java"
            , "com\\sunsharing\\xhsare\\management\\database\\utils\\SpringUtil.java"
            , "com\\sunsharing\\xhsare\\management\\database\\config\\DataSourceConfig.java"
            , "com\\sunsharing\\xhsare\\management\\database\\config\\DataSourcePropertiesConfig.java"
            , "com\\sunsharing\\xhsare\\management\\database\\config\\DynamicDataSource.java"
            , "com\\sunsharing\\xhsare\\management\\database\\config\\WebMvcConfiguration.java"
            , "com\\sunsharing\\xhsare\\management\\database\\DatabaseApplication.java"
            , "com\\sunsharing\\xhsare\\management\\database\\dao\\IDataBaseMapper.xml"
            , "com\\sunsharing\\xshare\\management\\executor\\ExceptionResolver.java"
            , "com\\sunsharing\\xshare\\management\\server\\dao\\search\\ResourceMapper.xml"
            , "com\\sunsharing\\xshare\\management\\server\\dao\\monitor\\DataShareMonitorMapper.xml"
            , "com\\sunsharing\\xshare\\management\\platform\\common\\service\\UploadService.java"
            , "com\\sunsharing\\xshare\\management\\log\\service\\LoadConsumer.java"
            , "com\\sunsharing\\xshare\\management\\log\\service\\impl\\MonitorDataConsumerAdapter.java"
            , "com\\sunsharing\\xshare\\management\\common\\utils\\FileUtils.java"
            , "com\\sunsharing\\xshare\\management\\server\\common\\XshareExcelUtil.java"
            , "com\\sunsharing\\xshare\\management\\server\\service\\resource\\PublishService.java"
            , "com\\sunsharing\\xshare\\management\\api\\multilevelplatform\\request\\MultiLevelPlatformResourceRequest.java"
            , "com\\sunsharing\\xshare\\management\\server\\service\\form\\UserSchemaPlugin.java"
            , "com\\sunsharing\\xshare\\management\\api\\openplatform\\resource\\view\\HeaderView.java"
            , "com\\sunsharing\\xshare\\management\\api\\filex\\view\\FilexConfigView.java"//todo 临时记录，需要写到程序中
    );

    public static void main(String[] args) throws IOException {
        String sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log\\src\\main\\java";
        String targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-common\\src\\main\\java";
//        scanInterfaceReturnStringMethod();
        scanAllInterfaceReturnStringMethod();
//        testConvertApiAnnotation();
//        testConvertClassAnnotation();
//        testPrintImport(sourceFolder, "com.sunsharing.xshare.framework.web.mvc.response.ShareResponseObject");
//        testPrintAnnotation(sourceFolder, "Scheduled");
        startMigrate();
    }

    private static void scanAllInterfaceReturnStringMethod() throws IOException {
        Set<String> allPath = Sets.newHashSet(
                "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-common\\src\\main\\java",
                "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api\\src\\main\\java",
                "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log-api\\src\\main\\java",
                "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-log\\src\\main\\java");
        List<Path> sourcePathList = Lists.newArrayList();
        for (String path : allPath) {
            getAllFile(Paths.get(path), sourcePathList);
        }
        for (Path path : sourcePathList) {
            if (path.toString().contains(".java")) {
                System.out.println("scan-path: " + path.toString());
                CommonTokenStream commonTokenStream = getTokenStreamFromFile(path.toString());
                JavaParser javaParser = new JavaParser(commonTokenStream);
                ParseTree parseTree = javaParser.compilationUnit();

                ParseTreeWalker walker = new ParseTreeWalker();
                GetInterfaceReturnStrMethodListener getInterfaceReturnStrMethodListener = new GetInterfaceReturnStrMethodListener();
                walker.walk(getInterfaceReturnStrMethodListener, parseTree);
                if (getInterfaceReturnStrMethodListener.methodSet.size() > 0) {
                    CommonConstant.methodMap.put(getInterfaceReturnStrMethodListener.className, getInterfaceReturnStrMethodListener.methodSet);
                }
            }
        }
        for (Map.Entry<String, Set<String>> entry : CommonConstant.methodMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    private static void scanInterfaceReturnStringMethod() {
        String code = "/*\n" +
                " * @(#) RegisterDatabaseApi\n" +
                " * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究\n" +
                " *\n" +
                " * <br> Copyright:  Copyright (c) 2018\n" +
                " * <br> Company:厦门畅享信息技术有限公司\n" +
                " * <br> @author Administrator\n" +
                " * <br> 2018-11-05 16:44:57\n" +
                " */\n" +
                "\n" +
                "package com.sunsharing.xshare.management.api.database;\n" +
                "\n" +
                "import com.sunsharing.xshare.framework.web.rest.RestApi;\n" +
                "import com.sunsharing.xshare.management.api.database.request.TableNameRequest;\n" +
                "import com.sunsharing.xshare.management.api.database.view.ApplyDatabaseTestView;\n" +
                "import com.sunsharing.xshare.management.api.database.view.ApplyTestView;\n" +
                "import com.sunsharing.xshare.management.api.database.view.DatabaseDataView;\n" +
                "import com.sunsharing.xshare.management.api.database.view.DatabaseTestResultView;\n" +
                "import com.sunsharing.xshare.management.api.database.view.MetadataView;\n" +
                "import com.sunsharing.xshare.management.api.database.view.RegisterTestView;\n" +
                "import com.sunsharing.xshare.management.api.database.view.TableNameView;\n" +
                "import com.sunsharing.xshare.management.api.filex.view.MessageResponseView;\n" +
                "import com.sunsharing.xshare.management.api.filex.view.TestProcessView;\n" +
                "import com.sunsharing.xshare.management.api.resource.register.request.DatabaseRequest;\n" +
                "import com.sunsharing.xshare.management.api.resource.register.request.ResourceRequest;\n" +
                "import com.sunsharing.xshare.management.api.resource.register.view.ResourceDetailView;\n" +
                "\n" +
                "import org.springframework.web.bind.annotation.PathVariable;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RequestMethod;\n" +
                "import org.springframework.web.bind.annotation.RequestParam;\n" +
                "import org.springframework.web.bind.annotation.ResponseBody;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "/**\n" +
                " * Created by cyc on 2018/11/5.\n" +
                " */\n" +
                "@RestApi\n" +
                "public interface RegisterDatabaseApi {\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/database/table/name/all/list\", method = RequestMethod.POST)\n" +
                "    TableNameView queryDatabaseTable(@RequestBody TableNameRequest tableNameRequest);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/database/table/column\", method = RequestMethod.GET)\n" +
                "    List<MetadataView> queryDatabaseMetadata(@RequestParam(\"accessPointId\") String nodeId, @RequestParam(\"dataSourceId\") String\n" +
                "        dataSourceId, @RequestParam(\"tableName\") String tableName, @RequestParam(required = false, name = \"resourceId\") String resourceId);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/database/register/test/view/{resourceId}\", method = RequestMethod.GET)\n" +
                "    RegisterTestView getDatabaseRegisterTestView(@PathVariable(\"resourceId\") String resourceId);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/database/test/view/{resourceId}/{userId}\", method = RequestMethod.GET)\n" +
                "    DatabaseTestResultView databaseRegisterTest(@PathVariable(\"resourceId\") String resourceId, @PathVariable(\"userId\") String userId);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/database/apply/test/{resourceId}/{userId}\", method = RequestMethod.GET)\n" +
                "    ApplyDatabaseTestView databaseApplyTest(@PathVariable(\"resourceId\") String resourceId, @RequestParam(name = \"applyId\", required = false)\n" +
                "        String applyId, @PathVariable(\"userId\") String userId);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/database/apply/test/view/{resourceId}\", method = RequestMethod.GET)\n" +
                "    ApplyTestView getDatabaseApplyTestView(@PathVariable(\"resourceId\") String resourceId, @RequestParam(name = \"applyId\", required = false)\n" +
                "        String applyId);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/database/table/data/{resourceId}\", method = RequestMethod.GET)\n" +
                "    DatabaseDataView queryTableDate(@PathVariable(\"resourceId\") String resourceId);\n" +
                "\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/resource/add\", method = RequestMethod.POST)\n" +
                "    String addResource(@RequestBody ResourceRequest<DatabaseRequest> request);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/resource/add/cache\", method = RequestMethod.POST)\n" +
                "    MessageResponseView addResourceCache(@RequestBody ResourceRequest<DatabaseRequest> request);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/resource/edit\", method = RequestMethod.POST)\n" +
                "    String updateResource(@RequestBody ResourceRequest<DatabaseRequest> request);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/resource/edit/cache\", method = RequestMethod.POST)\n" +
                "    MessageResponseView updateResourceCache(@RequestBody ResourceRequest<DatabaseRequest> request);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/resource/delete\", method = RequestMethod.POST)\n" +
                "    String deleteResource(@RequestBody List<String> resourceId);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/resource/view/{resourceId}\", method = RequestMethod.GET)\n" +
                "    ResourceDetailView getResource(@PathVariable(\"resourceId\") String resourceId);\n" +
                "\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/database/last/task/log\", method = RequestMethod.GET)\n" +
                "    List<TestProcessView> getLastTaskLog(@RequestParam(name = \"applyId\") String applyId);\n" +
                "}\n";
        CommonTokenStream commonTokenStream = getTokenStream(code);
        JavaParser javaParser = new JavaParser(commonTokenStream);
        ParseTree parseTree = javaParser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        GetInterfaceReturnStrMethodListener getInterfaceReturnStrMethodListener = new GetInterfaceReturnStrMethodListener();
        walker.walk(getInterfaceReturnStrMethodListener, parseTree);
        System.out.println(getInterfaceReturnStrMethodListener.className);
        System.out.println(getInterfaceReturnStrMethodListener.methodSet);
        CommonConstant.methodMap.put(getInterfaceReturnStrMethodListener.className, getInterfaceReturnStrMethodListener.methodSet);
    }

    private static void startMigrate() throws IOException {
        //迁移xshare-management-common模块
        String sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-common\\src\\main\\java";
        String targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-common\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\alarm";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\alarm";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-sms-server", true);

        //迁移xshare-management-log-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log-api\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-log-api\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-log");

        //迁移xshare-management-api-log
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-log\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-log\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-log-poseidon
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log-poseidon\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-log-poseidon\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-log");

        //迁移xshare-management-log
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-log\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-api-external
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-external\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-external\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-api-external
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-external\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\multilevelplatform";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-external\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\multilevelplatform";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-remote-platform");

        //迁移xshare-management-api-external
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-external\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\openplatform";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-external\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\openplatform";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-open-platform");

        //迁移xshare-management-api-aggregation
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-aggregation\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-aggregation\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-aggregation-database");

        //迁移xshare-management-api-job
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-job\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-job\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\register";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\register";
        testCodeMigrate(sourceFolder, targetFolder, "poseidon-filex-server", true);

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\test";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\test";
        testCodeMigrate(sourceFolder, targetFolder, "poseidon-filex-server", true);

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\database";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\database";
        testCodeMigrate(sourceFolder, targetFolder, "poseidon-db-server", true);

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\service";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\service";
        testCodeMigrate(sourceFolder, targetFolder, "poseidon-service-server", true);

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\stream";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\stream";
        testCodeMigrate(sourceFolder, targetFolder, "poseidon-stream-server", true);

        //迁移xshare-management-api-poseidon
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-poseidon\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-poseidon\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-server
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-server\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-server\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-platform
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-platform\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-platform\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-platform静态资源
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-platform\\src\\main\\webapp";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-platform\\src\\main\\resources\\static";
        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-maintenance
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-maintenance\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-maintenance\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-maintenance静态资源
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-maintenance\\src\\main\\webapp";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-maintenance\\src\\main\\resources\\static";
        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-aggregation-database
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-aggregation-database\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-aggregation-database\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-job
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-job\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-job\\src\\main\\java";
        testCodeMigrate(sourceFolder, targetFolder, "");
    }

    private static void testCodeMigrate(String sourceFolder, String targetFolder, String clientServerName) throws IOException {
        List<Path> sourcePathList = Lists.newArrayList();
//        getSingleFile(sourcePathList);
        getAllFile(Paths.get(sourceFolder), sourcePathList);
        handlerSourceFilePath2TargetPath(sourcePathList, sourceFolder, targetFolder, clientServerName);
    }

    private static void testCodeMigrate(String sourceFolder, String targetFolder, String clientServerName, boolean isNonStandardMsg) throws IOException {
        List<Path> sourcePathList = Lists.newArrayList();
//        getSingleFile(sourcePathList);
        getAllFile(Paths.get(sourceFolder), sourcePathList);
        handlerSourceFilePath2TargetPath(sourcePathList, sourceFolder, targetFolder, clientServerName, isNonStandardMsg);
    }

    private static void getSingleFile(List<Path> sourcePathList) {
        Path path = Paths.get("F:\\SunSharing_SourceCode\\xshare-management\\xshare-management-log\\src\\main\\java\\com\\sunsharing\\xshare\\management\\log\\controller\\FileXTestLogController.java");
        sourcePathList.add(path);
    }

    private static void testPrintAnnotation(String sourceFolder, String annotationName) throws IOException {
        List<Path> sourcePathList = Lists.newArrayList();
        getAllFile(Paths.get(sourceFolder), sourcePathList);
        for (Path path : sourcePathList) {
            String fileName = path.toString();
            if (!fileName.contains(".java")) {
                continue;
            }
            CommonTokenStream commonTokenStream = getTokenStreamFromFile(fileName);

            JavaParser javaParser = new JavaParser(commonTokenStream);
            ParseTree parseTree = javaParser.compilationUnit();

            ParseTreeWalker walker = new ParseTreeWalker();
            PrintAnnotationListener printAnnotationListener = new PrintAnnotationListener(annotationName);
            walker.walk(printAnnotationListener, parseTree);
            if (printAnnotationListener.isHasTargetAnnotation) {
                System.out.println(printAnnotationListener.className + " : " + fileName);
            }
        }
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
        String code = "/*\n" +
                " * @(#) DatabaseXTaskQuerySchemeService\n" +
                " * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究\n" +
                " *\n" +
                " * <br> Copyright:  Copyright (c) 2018\n" +
                " * <br> Company:厦门畅享信息技术有限公司\n" +
                " * <br> @author Administrator\n" +
                " * <br> 2018-11-23 22:16:05\n" +
                " */\n" +
                "\n" +
                "package com.sunsharing.xshare.management.log.service.audit;\n" +
                "\n" +
                "import com.github.pagehelper.PageHelper;\n" +
                "import com.github.pagehelper.PageInfo;\n" +
                "import com.sunsharing.xshare.framework.core.assertion.ParameterAssertor;\n" +
                "import com.sunsharing.xshare.framework.core.lang.StringUtils;\n" +
                "import com.sunsharing.xshare.framework.web.converter.IConverter;\n" +
                "import com.sunsharing.xshare.management.api.log.monitor.DataSharingApi;\n" +
                "import com.sunsharing.xshare.management.api.log.monitor.MetadataApi;\n" +
                "import com.sunsharing.xshare.management.api.log.monitor.request.QueryDataRecordDetailsRequest;\n" +
                "import com.sunsharing.xshare.management.api.log.monitor.request.QueryDbDataRecordTaskCodeSetRequest;\n" +
                "import com.sunsharing.xshare.management.api.log.monitor.request.QueryRecordRequest;\n" +
                "import com.sunsharing.xshare.management.common.constant.BooleanType;\n" +
                "import com.sunsharing.xshare.management.common.converter.DatabaseXDataRecordConverter;\n" +
                "import com.sunsharing.xshare.management.common.converter.IServerConverter;\n" +
                "import com.sunsharing.xshare.management.common.message.request.DatabaseXQuerySchemeParam;\n" +
                "import com.sunsharing.xshare.management.common.message.request.PageRequest;\n" +
                "import com.sunsharing.xshare.management.common.message.view.PageDataView;\n" +
                "import com.sunsharing.xshare.management.log.api.monitor.request.DatabaseXRecordRequest;\n" +
                "import com.sunsharing.xshare.management.log.dao.RedisQuerySchemeDao;\n" +
                "import com.sunsharing.xshare.management.request.ApplyColumnRequest;\n" +
                "import com.sunsharing.xshare.management.vo.ApplyColumn;\n" +
                "import com.sunsharing.xshare.management.vo.DbXDataRecordQueryParam;\n" +
                "import com.sunsharing.xshare.management.vo.QueryScheme;\n" +
                "import com.sunsharing.xshare.management.vo.QuerySchemeRecordDetail;\n" +
                "\n" +
                "import org.apache.commons.collections.MapUtils;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "import java.util.Collections;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "import javax.inject.Inject;\n" +
                "import javax.inject.Named;\n" +
                "\n" +
                "/**\n" +
                " * Created by chenlh on 2018/11/23.\n" +
                " */\n" +
                "@Service\n" +
                "public class DatabaseXTaskQuerySchemeService implements IDeTaskQueryService<DatabaseXQuerySchemeParam> {\n" +
                "    @Inject\n" +
                "    @Named(value = \"redisDatabaseXQuerySchemeDao\")\n" +
                "    private RedisQuerySchemeDao redisQuerySchemeDao;\n" +
                "    @Inject\n" +
                "    private FileXTaskQuerySchemeService fileXTaskQuerySchemeService;\n" +
                "    @Inject\n" +
                "    private MetadataApi metadataService;\n" +
                "    @Inject\n" +
                "    private DataSharingApi dataSharingMapper;\n" +
                "    @Inject\n" +
                "    private DatabaseXDataRecordConverter databaseXDataRecordConverter;\n" +
                "\n" +
                "    @Override\n" +
                "    public RedisQuerySchemeDao determineRedisQuerySchemeDao() {\n" +
                "        return redisQuerySchemeDao;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void beforeCreate(DatabaseXQuerySchemeParam param) {\n" +
                "        //如果存在数据主键查询条件，检验方案ID和数据记录表是否存在\n" +
                "        if (MapUtils.isNotEmpty(param.getDataPrimaryKeyParams())) {\n" +
                "            String schemeId = param.getSchemeId();\n" +
                "            ParameterAssertor.isNotEmpty(schemeId, \"交换方案ID为空，无法通过数据数据主键条件筛选交换日志\");\n" +
                "            getDataRecordTableName(schemeId);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public List<QueryScheme.Record> queryRecords(String tableName, DatabaseXQuerySchemeParam param) {\n" +
                "        return dataSharingMapper.queryTaskQuerySchemeRecords(new QueryRecordRequest(tableName, param));\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public List<QueryScheme.Record> filterQueryRecords(String tableName, List<QueryScheme.Record> records, DatabaseXQuerySchemeParam\n" +
                "        param) {\n" +
                "        HashMap dataPrimaryKeyParams = param.getDataPrimaryKeyParams();\n" +
                "        if (MapUtils.isEmpty(dataPrimaryKeyParams)) {\n" +
                "            return records;\n" +
                "        }\n" +
                "\n" +
                "        String schemeId = param.getSchemeId();\n" +
                "        List<ApplyColumn> columns = queryApplyColumns(schemeId, BooleanType.TRUE.getStatus());\n" +
                "        ParameterAssertor.isNotEmpty(columns, \"获取数据库交换方案(方案ID:{})数据主键失败，请联系数据库管理员\", schemeId);\n" +
                "        List<DbXDataRecordQueryParam> params = constDataRecordQueryParams(columns, dataPrimaryKeyParams);\n" +
                "        if (params.isEmpty()) {\n" +
                "            return records;\n" +
                "        }\n" +
                "\n" +
                "        String dataRecordTableName = getDataRecordTableName(schemeId);\n" +
                "        //通过已查询到的任务ID集合和数据主键参数查询匹配的任务ID集\n" +
                "        List<String> taskCodes = records.stream().map(QueryScheme.Record::getTaskCode).collect(Collectors.toList());\n" +
                "        Set<String> matchTaskCodes =\n" +
                "            dataSharingMapper.queryDbDataRecordTaskCodeSet(new QueryDbDataRecordTaskCodeSetRequest(dataRecordTableName, taskCodes, params));\n" +
                "        return matchTaskCodes.isEmpty() ? Collections.emptyList()\n" +
                "            : records.stream().filter(record -> matchTaskCodes.contains(record.getTaskCode())).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public List<QuerySchemeRecordDetail> findRecordDetails(List<QueryScheme.Record> list) {\n" +
                "        return fileXTaskQuerySchemeService.findRecordDetails(list);\n" +
                "    }\n" +
                "\n" +
                "    public PageDataView<HashMap> findDataRecordDetails(PageRequest<DatabaseXRecordRequest> request) {\n" +
                "        DatabaseXRecordRequest param = request.getDataView();\n" +
                "        String schemeId = param.getSchemeId();\n" +
                "        IDeTaskQueryService.logger.debug(\"开始查询数据库交换任务的交换数据记录，方案ID：{}\", schemeId);\n" +
                "        List<ApplyColumn> columns = queryApplyColumns(schemeId, null);\n" +
                "        List<DbXDataRecordQueryParam> queryParams = constQueryParams(columns, param);\n" +
                "        List<String> queryColumnNames = constQueryColumnNames(columns);\n" +
                "        String dataRecordTableName = getDataRecordTableName(schemeId);\n" +
                "        PageInfo<HashMap> pageInfo = PageHelper.startPage(request.getPageView()).doSelectPageInfo(\n" +
                "            () -> dataSharingMapper.queryDataRecordDetails(new QueryDataRecordDetailsRequest(dataRecordTableName, queryColumnNames,\n" +
                "                param.getTaskCode(), queryParams))\n" +
                "        );\n" +
                "        pageInfo.setList(databaseXDataRecordConverter.convertList(pageInfo.getList()));\n" +
                "        return IServerConverter.constPageDataView(pageInfo);\n" +
                "    }\n" +
                "\n" +
                "    public String getDataRecordTableName(String schemeId) {\n" +
                "        return metadataService.getDataRecordTableName(schemeId);\n" +
                "    }\n" +
                "\n" +
                "    private List<String> constQueryColumnNames(List<ApplyColumn> columns) {\n" +
                "        List<String> queryColumnNames = columns.stream()\n" +
                "            .filter(item -> StringUtils.isNotEmpty(item.getCode()))\n" +
                "            .map(ApplyColumn::getCode)\n" +
                "            .collect(Collectors.toList());\n" +
                "        ParameterAssertor.isNotEmpty(queryColumnNames, \"获取数据库交换方案的数据项编码集合失败，请联系数据库管理员\");\n" +
                "        return queryColumnNames;\n" +
                "    }\n" +
                "\n" +
                "    private List<DbXDataRecordQueryParam> constQueryParams(List<ApplyColumn> columns, DatabaseXRecordRequest param) {\n" +
                "        HashMap dataPrimaryKeyParams = param.getDataPrimaryKeyParams();\n" +
                "        dataPrimaryKeyParams = dataPrimaryKeyParams == null ? new HashMap() : dataPrimaryKeyParams;\n" +
                "        List<ApplyColumn> dataPrimaryKeyColumns = columns.stream().filter(ApplyColumn::getDataPrimaryKey).collect(Collectors.toList());\n" +
                "        return constDataRecordQueryParams(dataPrimaryKeyColumns, dataPrimaryKeyParams);\n" +
                "    }\n" +
                "\n" +
                "    private List<ApplyColumn> filterNullColumns(List<ApplyColumn> columns, HashMap dataPrimaryKeyParams) {\n" +
                "        return columns.stream().filter(item -> {\n" +
                "            Object value = dataPrimaryKeyParams.get(item.getCode());\n" +
                "            if (value == null || (value instanceof String && StringUtils.isEmpty(value.toString()))) {\n" +
                "                return false;\n" +
                "            }\n" +
                "            return dataPrimaryKeyParams.containsKey(item.getCode());\n" +
                "        }).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    private List<DbXDataRecordQueryParam> constDataRecordQueryParams(List<ApplyColumn> dataPrimaryKeyColumns, HashMap paramMap) {\n" +
                "        dataPrimaryKeyColumns = filterNullColumns(dataPrimaryKeyColumns, paramMap);\n" +
                "        return IConverter.convertList(dataPrimaryKeyColumns, (column, drParam) -> {\n" +
                "            String key = column.getCode();\n" +
                "            drParam.setKey(key);\n" +
                "            drParam.setValue(paramMap.get(key));\n" +
                "        }, DbXDataRecordQueryParam.class);\n" +
                "    }\n" +
                "\n" +
                "    private List<ApplyColumn> queryApplyColumns(String schemeId, String dataPrimaryKey) {\n" +
                "        ApplyColumnRequest applyColumnParam = new ApplyColumnRequest();\n" +
                "        applyColumnParam.setSchemeId(schemeId);\n" +
                "        applyColumnParam.setDataPrimaryKey(dataPrimaryKey);\n" +
                "        return metadataService.findApplyColumns(applyColumnParam);\n" +
                "    }\n" +
                "}\n";
        CommonTokenStream commonTokenStream = getTokenStream(code);
        CodeInfo codeInfo = convertClassAnnotation(commonTokenStream, "test-server");
        System.out.println(codeInfo.getCode());
    }

    private static void testConvertApiAnnotation() {
        String code = "package com.sunsharing.xshare.management.server.service.form;\n" +
                "\n" +
                "import com.sunsharing.xshare.framework.core.converter.BeanUtils;\n" +
                "import com.sunsharing.xshare.framework.core.json.JsonBinder;\n" +
                "import com.sunsharing.xshare.framework.web.mvc.request.RequestObject;\n" +
                "import com.sunsharing.xshare.management.api.sys.view.UserRequestParams;\n" +
                "import com.sunsharing.xshare.management.common.adapter.BooleanAdapter2;\n" +
                "import com.sunsharing.xshare.management.common.adapter.StringArrayAdapter;\n" +
                "import com.sunsharing.xshare.management.common.entity.sys.Application;\n" +
                "import com.sunsharing.xshare.management.common.entity.sys.Role;\n" +
                "import com.sunsharing.xshare.management.server.controller.sys.UserController;\n" +
                "import com.sunsharing.xshare.management.server.dao.sys.UserDao;\n" +
                "import com.sunsharing.xshare.management.server.service.datacollect.home.HomeService;\n" +
                "import com.sunsharing.zeus.scurd.configure.service.GlobalParams;\n" +
                "import com.sunsharing.zeus.scurd.configure.service.ext.After;\n" +
                "import com.sunsharing.zeus.scurd.configure.service.ext.Before;\n" +
                "import com.sunsharing.zeus.scurd.configure.service.ext.Method;\n" +
                "import com.sunsharing.zeus.scurd.configure.service.ext.Round;\n" +
                "import com.sunsharing.zeus.scurd.configure.service.ext.Scurd;\n" +
                "\n" +
                "import org.springframework.core.env.Environment;\n" +
                "import org.springframework.stereotype.Component;\n" +
                "\n" +
                "import java.util.HashMap;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "import javax.inject.Inject;\n" +
                "import javax.xml.bind.annotation.XmlAccessType;\n" +
                "import javax.xml.bind.annotation.XmlAccessorType;\n" +
                "import javax.xml.bind.annotation.XmlElement;\n" +
                "import javax.xml.bind.annotation.XmlRootElement;\n" +
                "import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;\n" +
                "\n" +
                "@Scurd(schemaKey = \"T_SYS_USER\")\n" +
                "@Component\n" +
                "public class UserSchemaPlugin {\n" +
                "\n" +
                "    @Inject\n" +
                "    private UserDao userDao;\n" +
                "\n" +
                "    @Inject\n" +
                "    private HomeService homeService;\n" +
                "\n" +
                "    @Inject\n" +
                "    private UserController userController;\n" +
                "    @Inject\n" +
                "    private Environment environment;\n" +
                "\n" +
                "\n" +
                "    @Round(method = Method.save)\n" +
                "    public void afterSave(Map reqData) {\n" +
                "        String jsonStr = JsonBinder.toJson(reqData);\n" +
                "        ScurdUserRequest scurdUserRequest = JsonBinder.fromJson(jsonStr, ScurdUserRequest.class);\n" +
                "        UserRequestParams userRequestParams = BeanUtils.copyProperties(scurdUserRequest, UserRequestParams.class);\n" +
                "        if (reqData.containsKey(GlobalParams.id)) {\n" +
                "            userRequestParams.setUserId(reqData.get(GlobalParams.id).toString());\n" +
                "            userController.updateUser(new RequestObject<>(userRequestParams));\n" +
                "        } else {\n" +
                "            userController.saveUser(new RequestObject<>(userRequestParams));\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    @Before(method = Method.getSchema)\n" +
                "    public void beforeGetSchema(Map reqData) {\n" +
                "        System.out.println(reqData);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 查询完成后，添加关联表\n" +
                "     * @param reqData 查询条件字段\n" +
                "     * @param result 查询的结果字段\n" +
                "     */\n" +
                "    @After(method = Method.getSchema)\n" +
                "    public void afterSearch(Map reqData, Map result) {\n" +
                "        List<HashMap> list = (List<HashMap>) result.get(\"columns\");\n" +
                "        if (reqData.containsKey(GlobalParams.id)) {\n" +
                "            StringBuilder sbId = new StringBuilder();\n" +
                "            StringBuilder sbName = new StringBuilder();\n" +
                "\n" +
                "            String id = String.valueOf(reqData.get(GlobalParams.id));\n" +
                "            Role role = userDao.queryRoleByUserId(id);\n" +
                "            List<Application> applicationList = homeService.queryApplicationListByUserId(id);\n" +
                "            for (Application app : applicationList) {\n" +
                "                sbId.append(app.getId()).append(\",\");\n" +
                "                sbName.append(app.getName()).append(\",\");\n" +
                "            }\n" +
                "            String applicationId = sbId.toString().substring(0, sbId.length() - 1);\n" +
                "            String applicationName = sbName.toString().substring(0, sbName.length() - 1);\n" +
                "\n" +
                "\n" +
                "            for (HashMap m : list) {\n" +
                "                if (\"授权应用\".equals(m.get(\"column_name\"))) {\n" +
                "                    m.put(\"val\", applicationId);\n" +
                "                    m.put(\"label\", applicationName);\n" +
                "\n" +
                "                }\n" +
                "                if (role != null && \"用户角色\".equals(m.get(\"column_name\"))) {\n" +
                "                    m.put(\"val\", role.getId());\n" +
                "                    m.put(\"label\", role.getName());\n" +
                "                }\n" +
                "                if (\"密码\".equals(m.get(\"column_name\"))) {\n" +
                "                    m.remove(\"val\");\n" +
                "                    m.remove(\"label\");\n" +
                "                    m.remove(\"detail_label\");\n" +
                "                }\n" +
                "            }\n" +
                "        } else {\n" +
                "            for (HashMap m : list) {\n" +
                "                if (\"密码\".equals(m.get(\"column_name\"))) {\n" +
                "                    m.put(\"val\", environment.getProperty(\"xshare.system.user.default.password\"));\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    @XmlRootElement(name = \"scurdUserRequest\")\n" +
                "    @XmlAccessorType(XmlAccessType.FIELD)\n" +
                "    public static class ScurdUserRequest {\n" +
                "        @XmlJavaTypeAdapter(StringArrayAdapter.class)\n" +
                "        @XmlElement(name = \"APPLICATIONS\")\n" +
                "        private String[] applications;\n" +
                "\n" +
                "        @XmlElement(name = \"ROLES\")\n" +
                "        private String roles;\n" +
                "\n" +
                "        @XmlElement(name = \"DEPARTMENT_ID\")\n" +
                "        private String departmentId;\n" +
                "\n" +
                "        @XmlElement(name = \"USER_NAME\")\n" +
                "        private String userName;\n" +
                "\n" +
                "        @XmlElement(name = \"OLD_PASSWORD\")\n" +
                "        private String oldPassword;\n" +
                "\n" +
                "        @XmlElement(name = \"PASSWORD\")\n" +
                "        private String password;\n" +
                "\n" +
                "        @XmlElement(name = \"REAL_NAME\")\n" +
                "        private String realName;\n" +
                "\n" +
                "        @XmlElement(name = \"ID_CARD\")\n" +
                "        private String idCard;\n" +
                "\n" +
                "        @XmlElement(name = \"MOBILE\")\n" +
                "        private String mobile;\n" +
                "\n" +
                "        @XmlElement(name = \"TELEPHONE\")\n" +
                "        private String telephone;\n" +
                "\n" +
                "        @XmlElement(name = \"EMAIL\")\n" +
                "        private String email;\n" +
                "\n" +
                "        @XmlElement(name = \"SEQ_NO\")\n" +
                "        private Integer seqNo;\n" +
                "\n" +
                "        @XmlJavaTypeAdapter(BooleanAdapter2.class)\n" +
                "        @XmlElement(name = \"DISABLED\")\n" +
                "        private Boolean disabled;\n" +
                "\n" +
                "        public ScurdUserRequest() {\n" +
                "        }\n" +
                "\n" +
                "        public String[] getApplications() {\n" +
                "            return applications;\n" +
                "        }\n" +
                "\n" +
                "        public void setApplications(String[] applications) {\n" +
                "            this.applications = applications;\n" +
                "        }\n" +
                "\n" +
                "        public String getRoles() {\n" +
                "            return roles;\n" +
                "        }\n" +
                "\n" +
                "        public void setRoles(String roles) {\n" +
                "            this.roles = roles;\n" +
                "        }\n" +
                "\n" +
                "        public String getDepartmentId() {\n" +
                "            return departmentId;\n" +
                "        }\n" +
                "\n" +
                "        public void setDepartmentId(String departmentId) {\n" +
                "            this.departmentId = departmentId;\n" +
                "        }\n" +
                "\n" +
                "        public String getUserName() {\n" +
                "            return userName;\n" +
                "        }\n" +
                "\n" +
                "        public void setUserName(String userName) {\n" +
                "            this.userName = userName;\n" +
                "        }\n" +
                "\n" +
                "        public String getOldPassword() {\n" +
                "            return oldPassword;\n" +
                "        }\n" +
                "\n" +
                "        public void setOldPassword(String oldPassword) {\n" +
                "            this.oldPassword = oldPassword;\n" +
                "        }\n" +
                "\n" +
                "        public String getPassword() {\n" +
                "            return password;\n" +
                "        }\n" +
                "\n" +
                "        public void setPassword(String password) {\n" +
                "            this.password = password;\n" +
                "        }\n" +
                "\n" +
                "        public String getRealName() {\n" +
                "            return realName;\n" +
                "        }\n" +
                "\n" +
                "        public void setRealName(String realName) {\n" +
                "            this.realName = realName;\n" +
                "        }\n" +
                "\n" +
                "        public String getIdCard() {\n" +
                "            return idCard;\n" +
                "        }\n" +
                "\n" +
                "        public void setIdCard(String idCard) {\n" +
                "            this.idCard = idCard;\n" +
                "        }\n" +
                "\n" +
                "        public String getMobile() {\n" +
                "            return mobile;\n" +
                "        }\n" +
                "\n" +
                "        public void setMobile(String mobile) {\n" +
                "            this.mobile = mobile;\n" +
                "        }\n" +
                "\n" +
                "        public String getTelephone() {\n" +
                "            return telephone;\n" +
                "        }\n" +
                "\n" +
                "        public void setTelephone(String telephone) {\n" +
                "            this.telephone = telephone;\n" +
                "        }\n" +
                "\n" +
                "        public String getEmail() {\n" +
                "            return email;\n" +
                "        }\n" +
                "\n" +
                "        public void setEmail(String email) {\n" +
                "            this.email = email;\n" +
                "        }\n" +
                "\n" +
                "        public Integer getSeqNo() {\n" +
                "            return seqNo;\n" +
                "        }\n" +
                "\n" +
                "        public void setSeqNo(Integer seqNo) {\n" +
                "            this.seqNo = seqNo;\n" +
                "        }\n" +
                "\n" +
                "        public Boolean getDisabled() {\n" +
                "            return disabled;\n" +
                "        }\n" +
                "\n" +
                "        public void setDisabled(Boolean disabled) {\n" +
                "            this.disabled = disabled;\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        CodeInfo codeInfo = convertApiAnnotation(code, "test-server");
        System.out.println(codeInfo.getCode());
    }

    private static void handlerSourceFilePath2TargetPath(List<Path> pathList, String sourceFolder, String targetFolder,
                                                         String clientServerName) throws IOException {
        for (Path sourcePath : pathList) {
            String targetPathStr = targetFolder + sourcePath.toString().replace(sourceFolder, "");
            if (targetPathStr.contains("xhsare")) {
                targetPathStr = targetPathStr.replace("xhsare", "xshare");
            }
            Path targetPath = Paths.get(targetPathStr);
            if (excludeFileName.contains(targetPath.getFileName().toString())) {
                System.out.println("exclude path: " + targetPathStr);
                continue;
            }
            boolean isExcludeFilePath = isExcludeFilePath(sourcePath.toString());
            if (isExcludeFilePath) {
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

    private static void handlerSourceFilePath2TargetPath(List<Path> pathList, String sourceFolder, String targetFolder,
                                                         String clientServerName, boolean isNonStandardMsg) throws IOException {
        for (Path sourcePath : pathList) {
            String targetPathStr = targetFolder + sourcePath.toString().replace(sourceFolder, "");
            if (targetPathStr.contains("xhsare")) {
                targetPathStr = targetPathStr.replace("xhsare", "xshare");
            }
            Path targetPath = Paths.get(targetPathStr);
            if (excludeFileName.contains(targetPath.getFileName().toString())) {
                System.out.println("exclude path: " + targetPathStr);
                continue;
            }
            boolean isExcludeFilePath = isExcludeFilePath(sourcePath.toString());
            if (isExcludeFilePath) {
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
                CodeInfo codeInfo = convertClassAnnotation(commonTokenStream, clientServerName, isNonStandardMsg);
                System.out.println("end convert code: " + codeInfo.getClassName());
                Files.write(targetPath, codeInfo.getCode().getBytes(Charsets.UTF_8));
            } else {
                Files.copy(sourcePath, targetPath);
            }
        }
    }

    private static boolean isExcludeFilePath(String sourceFilePath) {
        for (String path : excludeFilePath) {
            if (sourceFilePath.contains(path)) {
                return true;
            }
        }
        return false;
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

    public static CodeInfo convertClassAnnotation(CommonTokenStream commonTokenStream, String clientServerName, boolean isNonStandardMsg) {
        JavaParser javaParser = new JavaParser(commonTokenStream);
        ParseTree parseTree = javaParser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        ConvertAnnotationListener convertAnnotationListener = new ConvertAnnotationListener(commonTokenStream, clientServerName, isNonStandardMsg);
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
