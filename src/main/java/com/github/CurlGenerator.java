package com.github;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CurlGenerator {

    private static String CURL_TEMPLATE = "curl --location --request $method$ '$url$' \\\n" +
            "$headers:{header | --header '$header$' \\\n}$" +
            "$if(body)$-d '$body$'$endif$";

    public static void main(String[] args) {
        String url = "http://localhost:7006/auto-test/getShareList";
        Map<String, String> headers = new HashMap<>();
        headers.put("aa", "bb");
        headers.put("Content-Type", "application/json");
        headers.put("bbb", "asklgj");
        System.out.println(build(Method.POST, url, headers, "uuu82"));
    }

    public static String build(Method method, String url) {
        return build(method, url, null, null);
    }

    public static String build(Method method, String url, Map<String, String> headers) {
        return build(method, url, headers, null);
    }

    public static String build(Method method, String url, Map<String, String> headers, String body) {
        ST st = new ST(CURL_TEMPLATE, '$', '$');
        st.add("method", method.name());
        st.add("url", url);
        st.add("body", body);
        if (Objects.nonNull(headers)) {
            List<String> headerList = new ArrayList<>();
            headers.forEach((k, v) -> headerList.add(k + ": " + v));
            headerList.forEach(e -> st.add("headers", e));
        }
        String render = st.render();
        if (render.lastIndexOf("\n") != -1) {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                return render.substring(0, render.length() - 3);
            }
            return render.substring(0, render.length() - 2);
        }
        return render;
    }

    public enum Method {
        GET,
        POST
    }
}
