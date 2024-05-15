package com.yutak.vertx.kit;

public class TemplateKit {
    public static String templateSuffix(String templateFileName) {
        int index = templateFileName.lastIndexOf(".");
        if (index > 0) {
            return templateFileName.substring(index + 1).toLowerCase();
        }
        return null;
    }

    public static String buildTemplatePath(String basePath, String templateFileName) {
        if (StringKit.isEmpty(basePath)) {
            return templateFileName;
        }
        if (templateFileName.startsWith(basePath)) {
            return templateFileName;
        }
        return basePath + templateFileName;
    }
}
