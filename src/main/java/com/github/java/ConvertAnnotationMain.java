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
            "MyCasAuthenticationEntryPoint.java");
    private static final Set<String> excludeFilePath = Sets.newHashSet(
            "com\\sunsharing\\xshare\\management\\log\\exception\\ExceptionResolver.java"
            , "com\\sunsharing\\xshare\\management\\server\\dao\\search\\ResourceMapper.xml"
            , "com\\sunsharing\\xshare\\management\\server\\service\\catalog\\CatalogScheduledService.java"
            , "com\\sunsharing\\xshare\\management\\server\\service\\statistics\\DataSurveyScheduledService.java"
    );

    public static void main(String[] args) throws IOException {
        String sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-common\\src\\main\\java";
        String targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-common\\src\\main\\java";
//        scanInterfaceReturnStringMethod();
        scanAllInterfaceReturnStringMethod();
//        testConvertApiAnnotation();
//        testConvertClassAnnotation();
//        testPrintImport(sourceFolder, "com.sunsharing.xshare.framework.web.mvc.response.ShareResponseObject");
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
//        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\alarm";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\alarm";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-sms-server", true);

        //迁移xshare-management-log-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log-api\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-log-api\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-log");

        //迁移xshare-management-api-log
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-log\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-log\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-log-poseidon
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log-poseidon\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-log-poseidon\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-log");

        //迁移xshare-management-log
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-log\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-log\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "");

        //迁移xshare-management-api-external
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-external\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-external\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-api-external
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-external\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\multilevelplatform";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-external\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\multilevelplatform";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-remote-platform");

        //迁移xshare-management-api-external
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-external\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\openplatform";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-external\\src\\main\\java\\com\\sunsharing\\xshare\\management\\api\\openplatform";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-open-platform");

        //迁移xshare-management-api-aggregation
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-aggregation\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-aggregation\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-aggregation-database", true);

        //迁移xshare-management-api-job
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-job\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-job\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\register";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\register";
//        testCodeMigrate(sourceFolder, targetFolder, "poseidon-filex-server", true);

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\test";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\test";
//        testCodeMigrate(sourceFolder, targetFolder, "poseidon-filex-server", true);

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\database";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\database";
//        testCodeMigrate(sourceFolder, targetFolder, "poseidon-db-server", true);

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\service";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\service";
//        testCodeMigrate(sourceFolder, targetFolder, "poseidon-service-server", true);

        //迁移xshare-poseidon-api
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\stream";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-poseidon-api\\src\\main\\java\\com\\sunsharing\\xshare\\poseidon\\api\\stream";
//        testCodeMigrate(sourceFolder, targetFolder, "poseidon-stream-server", true);

        //迁移xshare-management-api-poseidon
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-api-poseidon\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-api-poseidon\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-server
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-server\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-server\\src\\main\\java";
//        testCodeMigrate(sourceFolder, targetFolder, "xshare-management-server");

        //迁移xshare-management-platform
        sourceFolder = "F:\\SunSharing_SourceCode\\master-xshare-management\\xshare-management\\xshare-management-platform\\src\\main\\java";
        targetFolder = "F:\\GithubProject\\xshare-management-boot\\xshare-management-platform\\src\\main\\java";
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
        String code = "package com.sunsharing.xshare.management.platform.filex;\n" +
                "\n" +
                "import com.sunsharing.xshare.framework.core.collection.CollectionUtils;\n" +
                "import com.sunsharing.xshare.framework.core.converter.BeanUtils;\n" +
                "import com.sunsharing.xshare.framework.core.exception.BusinessException;\n" +
                "import com.sunsharing.xshare.framework.core.log.LoggerAdapter;\n" +
                "import com.sunsharing.xshare.framework.core.log.LoggerManager;\n" +
                "import com.sunsharing.xshare.framework.core.message.MessageUtils;\n" +
                "import com.sunsharing.xshare.management.api.filex.ApplyFilexApi;\n" +
                "import com.sunsharing.xshare.management.api.filex.RegisterFilexApi;\n" +
                "import com.sunsharing.xshare.management.api.filex.request.*;\n" +
                "import com.sunsharing.xshare.management.api.filex.view.ApplyCountView;\n" +
                "import com.sunsharing.xshare.management.api.filex.view.FileApplyCommonView;\n" +
                "import com.sunsharing.xshare.management.api.filex.view.FileApplyDetailView;\n" +
                "import com.sunsharing.xshare.management.api.filex.view.MessageResponseView;\n" +
                "import com.sunsharing.xshare.management.api.sys.response.RoleView;\n" +
                "import com.sunsharing.xshare.management.common.constant.RoleType;\n" +
                "import com.sunsharing.xshare.management.common.constant.resource.ApplyType;\n" +
                "import com.sunsharing.xshare.management.common.constant.resource.CancelType;\n" +
                "import com.sunsharing.xshare.management.common.constant.resource.FieldType;\n" +
                "import com.sunsharing.xshare.management.common.constant.resource.ResourceType;\n" +
                "import com.sunsharing.xshare.management.common.exception.ApplyInfoCheckException;\n" +
                "import com.sunsharing.xshare.management.common.message.request.PageRequest;\n" +
                "import com.sunsharing.xshare.management.common.message.view.PageDataView;\n" +
                "import com.sunsharing.xshare.management.platform.auth.UserInfo;\n" +
                "import com.sunsharing.xshare.management.platform.auth.UserInfoService;\n" +
                "import com.sunsharing.xshare.management.platform.common.service.ItextService;\n" +
                "import com.sunsharing.xshare.management.platform.common.service.UploadService;\n" +
                "import com.sunsharing.xshare.management.platform.filex.converter.FilexApplyListResponseConverter;\n" +
                "import com.sunsharing.xshare.management.platform.filex.converter.FilexApplyRequestConverter;\n" +
                "import com.sunsharing.xshare.management.platform.filex.converter.FilexRegisterListRequestConverter;\n" +
                "import com.sunsharing.xshare.management.platform.filex.request.*;\n" +
                "import com.sunsharing.xshare.management.platform.filex.request.GreenChannelRequest;\n" +
                "import com.sunsharing.xshare.management.platform.filex.request.ResourceEvaluateRequest;\n" +
                "import com.sunsharing.xshare.management.platform.filex.service.CheckDataService;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.ApplyBacklogCount;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.ApplyCountFontView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.ApplyEvaluateView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.ApplyFormAddressView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.FileApplyAllListView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.FileApplyDetailFrontView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.FileInUseListView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.FilexApplyApproveListView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.FilexApplyCheckpendListView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.FilexApplyDisApproveListView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.FilexApplyRevokeListView;\n" +
                "import com.sunsharing.xshare.management.platform.filex.view.GreenChannelView;\n" +
                "\n" +
                "import org.springframework.http.ResponseEntity;\n" +
                "import org.springframework.stereotype.Controller;\n" +
                "import org.springframework.web.bind.annotation.PathVariable;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RequestMethod;\n" +
                "import org.springframework.web.bind.annotation.RequestParam;\n" +
                "import org.springframework.web.bind.annotation.ResponseBody;\n" +
                "\n" +
                "import java.io.File;\n" +
                "import java.io.IOException;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "import javax.inject.Inject;\n" +
                "import javax.validation.Valid;\n" +
                "\n" +
                "@Controller\n" +
                "public class ApplyFilexController {\n" +
                "\n" +
                "    private LoggerAdapter logger = LoggerManager.getLogger(getClass());\n" +
                "\n" +
                "    @Inject\n" +
                "    private ApplyFilexApi applyFilexApi;\n" +
                "\n" +
                "    @Inject\n" +
                "    private RegisterFilexApi filexRegisterApi;\n" +
                "\n" +
                "    @Inject\n" +
                "    private FilexRegisterListRequestConverter registerListRequestConverter;\n" +
                "\n" +
                "    @Inject\n" +
                "    private FilexApplyListResponseConverter filexApplyListResponseConverter;\n" +
                "\n" +
                "    @Inject\n" +
                "    private FilexApplyRequestConverter filexApplyRequestConverter;\n" +
                "\n" +
                "    @Inject\n" +
                "    private UserInfoService userInfoService;\n" +
                "\n" +
                "    @Inject\n" +
                "    private CheckDataService checkDataService;\n" +
                "    @Inject\n" +
                "    private ItextService itextService;\n" +
                "    @Inject\n" +
                "    private UploadService uploadService;\n" +
                "\n" +
                "    /**\n" +
                "     * 取消重点标记\n" +
                "     * @param applyId 申请id\n" +
                "     * @return 描述\n" +
                "     */\n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/filex/green/channel/delete\", method = RequestMethod.GET)\n" +
                "    public String deleteGreenChannel(@RequestParam String applyId) {\n" +
                "        applyFilexApi.deleteGreenChannel(applyId, userInfoService.getCurrentUser().getId());\n" +
                "        return \"取消重点标记成功\";\n" +
                "    }\n" +
                "    \n" +
                "    @ResponseBody\n" +
                "    @RequestMapping(value = \"/resource/delete\", method = RequestMethod.POST)\n" +
                "    public String deleteResource(@RequestBody List<String> resourceId) {\n" +
                "        return registerDatabaseApi.deleteResource(resourceId);\n" +
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
