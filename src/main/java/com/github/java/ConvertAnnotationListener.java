package com.github.java;

import com.github.java.antlr.JavaParser;
import com.github.java.antlr.JavaParserBaseListener;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ConvertAnnotationListener extends JavaParserBaseListener {

    private static final String LOG_ADAPTER = "LoggerAdapter";

    public String packageName;

    public String className;

    private String clientServerName;

    private boolean isNonStandardMsg;

    protected TokenStreamRewriter rewriter;

    protected BufferedTokenStream bufferedTokenStream;

    private String loggerAdapterName;

    private boolean isHasLoggerAdapter;

    private boolean isNeedReplaceLogger;

    private boolean isHasController;

    private boolean isRestApi;

    private boolean isNeedReplaceJaxb;

    private boolean isNeedImportShareResponse;

    private boolean isNeedReplaceShareResponse;

    private boolean isNeedImportJsonProperty;

    private boolean isNeedReplaceJsonProperty;

    private String type;

    private Set<String> deleteImportPackageSet = Sets.newHashSet(
            "org.springframework.stereotype.Controller",
            "org.springframework.web.bind.annotation.ResponseBody",
            "com.sunsharing.xshare.framework.core.log.LoggerAdapter",
            "com.sunsharing.xshare.framework.core.log.LoggerManager",
            "com.sunsharing.xshare.framework.web.rest.RestApi",
            "org.springframework.web.bind.annotation.ResponseBody",
            "com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector",
//            "com.sunsharing.xshare.framework.web.mvc.response.ShareResponseObject",
            "javax.xml.bind.annotation.XmlAccessType",
            "javax.xml.bind.annotation.XmlAccessorType",
            "javax.xml.bind.annotation.XmlElement",
            "javax.xml.bind.annotation.XmlRootElement",
            "javax.xml.bind.annotation.XmlElementWrapper",
            "javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter",
            "com.sunsharing.xshare.management.common.adapter.BooleanAdapter",
            "com.sunsharing.xshare.management.common.adapter.BooleanAdapter2",
            "com.sunsharing.xshare.management.common.adapter.Date8Adapter",
            "com.sunsharing.xshare.management.common.adapter.DateAdapter",
            "com.sunsharing.xshare.management.common.adapter.DateTimeAdapter",
            "com.sunsharing.xshare.management.common.adapter.MonitorStatusTypeAdapter",
            "com.sunsharing.xshare.management.common.adapter.QuerySchemeTypeAdapter",
            "com.sunsharing.xshare.management.common.adapter.ResourceTypeAdapter",
            "com.sunsharing.xshare.management.common.adapter.ResultStatusTypeAdapter",
            "com.sunsharing.xshare.management.common.adapter.SchemeTypeAdapter",
            "com.sunsharing.xshare.management.common.adapter.StringArrayAdapter",
            "com.sunsharing.xshare.management.common.adapter.TrimAdapter",
            "com.sunsharing.xshare.management.api.adapter.BooleanAdapter",
            "com.sunsharing.xshare.management.api.adapter.DateAdapter",
            "com.sunsharing.xshare.management.api.adapter.DateStringAdapter",
            "com.sunsharing.xshare.management.api.adapter.DateTimeAdapter",
            "com.sunsharing.xshare.management.api.adapter.InternetAccessRestrictionTypeAdapter",
            "com.sunsharing.xshare.management.api.adapter.TestTypeAdapter");

    private Set<String> needImportPackageSet = Sets.newHashSet();

    private static Map<String, String> adapterMap = Maps.newHashMap();

    private static Multimap<String, String> adapterImportPackageMap = ArrayListMultimap.create();

    static {
        adapterMap.put("@XmlJavaTypeAdapter(Date8Adapter.class)", "@JsonFormat(pattern = DateFormatPattern.DATE8_PATTERN)");
        adapterMap.put("@XmlJavaTypeAdapter(DateAdapter.class)", "@JsonFormat(pattern = DateFormatPattern.DATE_PATTERN)");
        adapterMap.put("@XmlJavaTypeAdapter(DateTimeAdapter.class)", "@JsonFormat(pattern = DateFormatPattern.DATE_TIME_PATTERN)");
        adapterMap.put("@XmlJavaTypeAdapter(DateTimestampAdapter.class)", "@JsonFormat(pattern = DateFormatPattern.DATE_TIMESTAMP_PATTERN)");
        adapterMap.put("@XmlJavaTypeAdapter(BooleanAdapter.class)", "@JsonSerialize(using = BooleanSerializer.class)\n" +
                "    @JsonDeserialize(using = BooleanDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(BooleanAdapter2.class)", "@JsonSerialize(using = Boolean2Serializer.class)\n" +
                "    @JsonDeserialize(using = Boolean2Deserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(MonitorStatusTypeAdapter.class)", "@JsonSerialize(using = MonitorStatusTypeSerializer.class)\n" +
                "    @JsonDeserialize(using = MonitorStatusTypeDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(QuerySchemeTypeAdapter.class)", "@JsonSerialize(using = QuerySchemeTypeSerializer.class)\n" +
                "    @JsonDeserialize(using = QuerySchemeTypeDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(ResourceTypeAdapter.class)", "@JsonSerialize(using = ResourceTypeSerializer.class)\n" +
                "    @JsonDeserialize(using = ResourceTypeDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(ResultStatusTypeAdapter.class)", "@JsonSerialize(using = ResultStatusTypeSerializer.class)\n" +
                "    @JsonDeserialize(using = ResultStatusTypeDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(SchemeTypeAdapter.class)", "@JsonSerialize(using = SchemeTypeSerializer.class)\n" +
                "    @JsonDeserialize(using = SchemeTypeDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(StringArrayAdapter.class)", "@JsonSerialize(using = StringArraySerializer.class)\n" +
                "    @JsonDeserialize(using = StringArrayDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(TrimAdapter.class)", "@JsonSerialize(using = TrimSerializer.class)\n" +
                "    @JsonDeserialize(using = TrimDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(DateStringAdapter.class)", "@JsonSerialize(using = DateStringSerializer.class)\n" +
                "    @JsonDeserialize(using = DateStringDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(InternetAccessRestrictionTypeAdapter.class)", "@JsonSerialize(using = InternetAccessRestrictionTypeSerializer.class)\n" +
                "    @JsonDeserialize(using = InternetAccessRestrictionTypeDeserializer.class)");
        adapterMap.put("@XmlJavaTypeAdapter(TestTypeAdapter.class)", "@JsonSerialize(using = TestTypeSerializer.class)\n" +
                "    @JsonDeserialize(using = TestTypeDeserializer.class)");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(Date8Adapter.class)", "\nimport com.fasterxml.jackson.annotation.JsonFormat;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(Date8Adapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.DateFormatPattern;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateAdapter.class)", "\nimport com.fasterxml.jackson.annotation.JsonFormat;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.DateFormatPattern;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateTimeAdapter.class)", "\nimport com.fasterxml.jackson.annotation.JsonFormat;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateTimeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.DateFormatPattern;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateTimestampAdapter.class)", "\nimport com.fasterxml.jackson.annotation.JsonFormat;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateTimestampAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.DateFormatPattern;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(BooleanAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(BooleanAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(BooleanAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.BooleanDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(BooleanAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.BooleanSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(BooleanAdapter2.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(BooleanAdapter2.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(BooleanAdapter2.class)", "\nimport com.sunsharing.xshare.management.common.adapter.Boolean2Deserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(BooleanAdapter2.class)", "\nimport com.sunsharing.xshare.management.common.adapter.Boolean2Serializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(MonitorStatusTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(MonitorStatusTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(MonitorStatusTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.MonitorStatusTypeDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(MonitorStatusTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.MonitorStatusTypeSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(QuerySchemeTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(QuerySchemeTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(QuerySchemeTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.QuerySchemeTypeDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(QuerySchemeTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.QuerySchemeTypeSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(ResourceTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(ResourceTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(ResourceTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.ResourceTypeDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(ResourceTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.ResourceTypeSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(ResultStatusTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(ResultStatusTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(ResultStatusTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.ResultStatusTypeDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(ResultStatusTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.ResultStatusTypeSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(SchemeTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(SchemeTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(SchemeTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.SchemeTypeDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(SchemeTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.SchemeTypeSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(StringArrayAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(StringArrayAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(StringArrayAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.StringArrayDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(StringArrayAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.StringArraySerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(TrimAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(TrimAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(TrimAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.TrimDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(TrimAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.TrimSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateStringAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateStringAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateStringAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.DateStringDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(DateStringAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.DateStringSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(InternetAccessRestrictionTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(InternetAccessRestrictionTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(InternetAccessRestrictionTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.InternetAccessRestrictionTypeDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(InternetAccessRestrictionTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.InternetAccessRestrictionTypeSerializer;");

        adapterImportPackageMap.put("@XmlJavaTypeAdapter(TestTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(TestTypeAdapter.class)", "\nimport com.fasterxml.jackson.databind.annotation.JsonSerialize;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(TestTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.TestTypeDeserializer;");
        adapterImportPackageMap.put("@XmlJavaTypeAdapter(TestTypeAdapter.class)", "\nimport com.sunsharing.xshare.management.common.adapter.TestTypeSerializer;");
    }

    public ConvertAnnotationListener(BufferedTokenStream tokenStream) {
        rewriter = new TokenStreamRewriter(tokenStream);
        this.bufferedTokenStream = tokenStream;
    }

    public ConvertAnnotationListener(BufferedTokenStream tokenStream, String clientServerName) {
        rewriter = new TokenStreamRewriter(tokenStream);
        this.bufferedTokenStream = tokenStream;
        this.clientServerName = clientServerName;
    }

    public ConvertAnnotationListener(BufferedTokenStream tokenStream, String clientServerName, boolean isNonStandardMsg) {
        rewriter = new TokenStreamRewriter(tokenStream);
        this.bufferedTokenStream = tokenStream;
        this.clientServerName = clientServerName;
        this.isNonStandardMsg = isNonStandardMsg;
    }

    @Override
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        if (isHasController) {
            String str = "\n\nimport org.springframework.web.bind.annotation.RestController;" +
                    "\nimport com.sunsharing.share.boot.framework.annotation.ShareRest;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (isHasLoggerAdapter) {
            String str = "\n\nimport lombok.extern.slf4j.Slf4j;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (isNeedReplaceLogger) {
            String str = "\n\nimport org.slf4j.Logger;\n" +
                    "import org.slf4j.LoggerFactory;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (isRestApi) {
            String str = "\n\nimport org.springframework.cloud.openfeign.FeignClient;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (isNeedImportShareResponse) {
            String str = "\n\nimport com.sunsharing.share.boot.framework.web.standard.entity.ShareResponse;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (isNeedImportJsonProperty) {
            String str = "\n\nimport com.fasterxml.jackson.annotation.JsonProperty;";
            rewriter.insertAfter(ctx.packageDeclaration().stop, str);
        }
        if (this.needImportPackageSet.size() > 0) {
            int i = 0;
            for (String packageStr : this.needImportPackageSet) {
                if (i == 0) {
                    packageStr = "\n" + packageStr;
                }
                rewriter.insertAfter(ctx.packageDeclaration().stop, packageStr);
                i++;
            }
        }
    }

    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        packageName = ctx.qualifiedName().getText();
    }

    @Override
    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {

    }

    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        if (Objects.nonNull(ctx.qualifiedName())) {
            String qualifiedName = ctx.qualifiedName().getText();
            if (deleteImportPackageSet.contains(qualifiedName)) {
                rewriter.delete(ctx.start, ctx.stop);
                deleteImportLineBreak(ctx);
            }
            if ("com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector".equals(qualifiedName)) {
                this.isNeedReplaceJaxb = true;
            }
            if ("com.sunsharing.xshare.framework.web.mvc.response.ShareResponseObject".equals(qualifiedName)) {
                this.isNeedReplaceShareResponse = true;
            }
            if ("com.sunsharing.xshare.management.common.message.response.ShareTestResponseObject".equals(qualifiedName)) {
                this.isNeedReplaceShareResponse = true;
            }
            if ("javax.xml.bind.annotation.XmlElement".equals(qualifiedName)) {
                this.isNeedReplaceJsonProperty = true;
                this.isNeedImportJsonProperty = true;
            }
            if ("javax.xml.bind.annotation.XmlElementWrapper".equals(qualifiedName)) {
                this.isNeedReplaceJsonProperty = true;
                this.isNeedImportJsonProperty = true;
            }
            if ("com.sunsharing.xshare.management.log.SysConfig".equals(qualifiedName)) {
                rewriter.replace(ctx.start, ctx.stop, "import com.sunsharing.xshare.management.log.config.SysConfig;");
            }
            if ("com.sunsharing.xshare.management.log.ConfigService".equals(qualifiedName)) {
                rewriter.replace(ctx.start, ctx.stop, "import com.sunsharing.xshare.management.log.config.ConfigService;");
            }
        }
    }

    private void deleteImportLineBreak(ParserRuleContext ctx) {
        Token token = ctx.getStop();
        int i = token.getTokenIndex();
        List<Token> tokenList = bufferedTokenStream.getHiddenTokensToRight(i);
        if (Objects.nonNull(tokenList)) {
            token = tokenList.get(0);
            if (Objects.nonNull(token)) {
                rewriter.replace(token, "");
            }
        }
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        if (this.isNeedReplaceShareResponse) {
            if (Objects.nonNull(ctx.typeType()) && Objects.nonNull(ctx.typeType().classOrInterfaceType())) {
                String typeName = ctx.typeType().classOrInterfaceType().getText();
                if ("ShareResponseObject".equals(typeName)) {
                    rewriter.replace(ctx.typeType().classOrInterfaceType().start,
                            ctx.typeType().classOrInterfaceType().stop,
                            "ShareResponse");
                    this.isNeedImportShareResponse = true;
                }
            }
        }
    }

    @Override
    public void enterClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (Objects.nonNull(ctx.annotation())) {
            String annotation = ctx.annotation().getText();
            if ("Controller".equals(ctx.annotation().qualifiedName().getText()) ||
                    "RestController".equals(ctx.annotation().qualifiedName().getText())) {
                String shareRestControllerStr;
                if (Objects.nonNull(ctx.annotation().elementValue())) {
                    String value = ctx.annotation().elementValue().expression().primary().literal().STRING_LITERAL().getText();
                    shareRestControllerStr = "@RestController($value$)\n" +
                            "@ShareRest";
                    ST st = new ST(shareRestControllerStr, '$', '$');
                    st.add("value", value);
                    shareRestControllerStr = st.render();
                } else {
                    shareRestControllerStr = "@RestController\n" +
                            "@ShareRest";
                }
                rewriter.replace(ctx.annotation().start, ctx.annotation().stop, shareRestControllerStr);
                this.isHasController = true;
            } else if ("@ResponseBody".equals(annotation)) {
                rewriter.delete(ctx.annotation().start, ctx.annotation().stop);
                deleteImportLineBreak(ctx.annotation());
            } else if ("@RestApi".equals(annotation)) {
                String feignClientStr = "@FeignClient(value = \"$clientServerName$\", contextId = \"$className$\")";
                if (this.isNonStandardMsg) {
                    feignClientStr = "@FeignClient(value = \"$clientServerName$\", contextId = \"$className$\", configuration = NonStandardConfiguration.class)";
                    this.needImportPackageSet.add("\nimport com.sunsharing.share.cloud.openfeign.NonStandardConfiguration;");
                }
                ST st = new ST(feignClientStr, '$', '$');
                st.add("clientServerName", clientServerName);
                st.add("className", packageName + "." + className);
                feignClientStr = st.render();
                rewriter.replace(ctx.annotation().start, ctx.annotation().stop, feignClientStr);
                this.isRestApi = true;
            } else if ("XmlRootElement".equals(ctx.annotation().qualifiedName().getText())) {
                rewriter.delete(ctx.annotation().start, ctx.annotation().stop);
                deleteImportLineBreak(ctx.annotation());
            } else if ("XmlAccessorType".equals(ctx.annotation().qualifiedName().getText())) {
                rewriter.delete(ctx.annotation().start, ctx.annotation().stop);
                deleteImportLineBreak(ctx.annotation());
            } else if ("XmlJavaTypeAdapter".equals(ctx.annotation().qualifiedName().getText())) {
                String javaTypeStr = ctx.annotation().getText();
                javaTypeStr = StringUtils.deleteWhitespace(javaTypeStr);
                javaTypeStr = javaTypeStr.replace("value=", "");
                if (adapterMap.containsKey(javaTypeStr)) {
                    String value = adapterMap.get(javaTypeStr);
                    rewriter.replace(ctx.annotation().start, ctx.annotation().stop, value);
                    List<String> needImportPackageList = (List<String>) adapterImportPackageMap.get(javaTypeStr);
                    this.needImportPackageSet.addAll(needImportPackageList);
                } else {
                    throw new RuntimeException(javaTypeStr + "不存在，需要处理");
                }
            }
        }
    }

    @Override
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        if (Objects.nonNull(ctx.classDeclaration())) {
            this.className = ctx.classDeclaration().IDENTIFIER().getText();
            this.type = ctx.classDeclaration().CLASS().getText();
        } else if (Objects.nonNull(ctx.interfaceDeclaration())) {
            this.className = ctx.interfaceDeclaration().IDENTIFIER().getText();
            this.type = ctx.interfaceDeclaration().INTERFACE().getText();
        } else if (Objects.nonNull(ctx.enumDeclaration())) {
            this.className = ctx.enumDeclaration().IDENTIFIER().getText();
            this.type = ctx.enumDeclaration().ENUM().getText();
        }
    }

    @Override
    public void exitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        if (isHasLoggerAdapter) {
            rewriter.insertBefore(ctx.start, "@Slf4j\n");
        }
    }

    @Override
    public void enterClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        if (this.isHasController) {
            ReplaceReturnStringVisitor replaceReturnStringVisitor = new ReplaceReturnStringVisitor(rewriter, type);
            ctx.accept(replaceReturnStringVisitor);
            this.isNeedImportShareResponse = this.isNeedImportShareResponse || replaceReturnStringVisitor.isReplaceReturnString;
        }
        if (this.isNeedReplaceJsonProperty) {
            CheckXmlWrapperVisitor checkXmlWrapperVisitor = new CheckXmlWrapperVisitor();
            ctx.accept(checkXmlWrapperVisitor);
            ReplaceXmlAnnotationVisitor replaceXmlAnnotationVisitor = new ReplaceXmlAnnotationVisitor(rewriter, bufferedTokenStream,
                    checkXmlWrapperVisitor.isHasXmlWrapper);
            ctx.accept(replaceXmlAnnotationVisitor);
        }
    }

    @Override
    public void enterInterfaceBody(JavaParser.InterfaceBodyContext ctx) {
        if (this.isRestApi) {
            ReplaceReturnStringVisitor replaceReturnStringVisitor = new ReplaceReturnStringVisitor(rewriter, type);
            ctx.accept(replaceReturnStringVisitor);
            this.isNeedImportShareResponse = this.isNeedImportShareResponse || replaceReturnStringVisitor.isReplaceReturnString;
        }
    }

    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        if (Objects.nonNull(ctx.typeType().classOrInterfaceType())) {
            String identifier = ctx.typeType().classOrInterfaceType().IDENTIFIER().get(0).getText();
            if (LOG_ADAPTER.equals(identifier)) {
                this.loggerAdapterName = ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getText();
                this.isHasLoggerAdapter = true;
                rewriter.delete(ctx.getParent().getParent().start, ctx.getParent().getParent().stop);
            }
        }
    }

    @Override
    public void enterConstDeclaration(JavaParser.ConstDeclarationContext ctx) {
        if (Objects.nonNull(ctx.typeType().classOrInterfaceType())) {
            String identifier = ctx.typeType().classOrInterfaceType().IDENTIFIER().get(0).getText();
            if (LOG_ADAPTER.equals(identifier)) {
                rewriter.replace(ctx.typeType().classOrInterfaceType().start, ctx.typeType().classOrInterfaceType().stop, "Logger");
                this.isNeedReplaceLogger = true;
            }
        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        if (this.isNeedReplaceJaxb) {
            GetVariableNameVisitor getVariableNameVisitor = new GetVariableNameVisitor(
                    "JaxbAnnotationIntrospector", "JacksonAnnotationIntrospector");
            ctx.accept(getVariableNameVisitor);
            if (StringUtils.isNotBlank(getVariableNameVisitor.sourceVarName)
                    && StringUtils.isNotBlank(getVariableNameVisitor.targetVarName)) {
                ReplaceVariableNameVisitor replaceVariableNameVisitor = new ReplaceVariableNameVisitor(this.rewriter,
                        getVariableNameVisitor.sourceVarName, getVariableNameVisitor.targetVarName);
                ctx.accept(replaceVariableNameVisitor);
            }
        }
//        if (this.isNeedReplaceShareResponse) {
//            ReplaceMethodCallVisitor replaceMethodCallVisitor = new ReplaceMethodCallVisitor(this.rewriter);
//            ctx.accept(replaceMethodCallVisitor);
//            this.isNeedImportShareResponse = this.isNeedImportShareResponse || replaceMethodCallVisitor.isNeedImportShareResponse;
//        }
    }

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        String expression = ctx.getText();
        if ("metadataService.getDataRecordTableName(schemeId)".equals(expression)) {
            rewriter.insertAfter(ctx.stop, ".getData()");
        }
    }

    @Override
    public void enterPrimary(JavaParser.PrimaryContext ctx) {
        if (Objects.nonNull(ctx.IDENTIFIER())) {
            if (isHasLoggerAdapter) {
                String identifier = ctx.IDENTIFIER().getText();
                if (this.loggerAdapterName.equals(identifier)) {
                    rewriter.replace(ctx.start, ctx.stop, "log");
                }
            }
            if (isNeedReplaceLogger) {
                String identifier = ctx.IDENTIFIER().getText();
                if ("LoggerManager".equals(identifier)) {
                    rewriter.replace(ctx.start, ctx.stop, "LoggerFactory");
                }
            }
        }
    }
}
