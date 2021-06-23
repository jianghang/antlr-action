package com.github.javaparser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;

public class PrettyPrintStarter {
    public static void main(String[] args) {
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration.setComment(new LineComment("A very cool class"));
        classOrInterfaceDeclaration.setName("MyClass");
        classOrInterfaceDeclaration.addField("String", "foo");

        DefaultPrinterConfiguration printerConfiguration = new DefaultPrinterConfiguration();
        printerConfiguration.addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.INDENT_CASE_IN_SWITCH));
        printerConfiguration.addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.PRINT_COMMENTS, false));
        DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter(printerConfiguration);
        System.out.println(defaultPrettyPrinter.print(classOrInterfaceDeclaration));
    }
}
