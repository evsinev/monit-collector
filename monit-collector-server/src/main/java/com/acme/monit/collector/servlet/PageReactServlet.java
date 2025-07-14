package com.acme.monit.collector.servlet;

import com.payneteasy.jetty.util.SafeHttpServlet;
import com.payneteasy.jetty.util.SafeServletRequest;
import com.payneteasy.jetty.util.SafeServletResponse;

public class PageReactServlet extends SafeHttpServlet {

    private final String             js;
    private final String             css;

    public PageReactServlet(String aAssetsIndexJsLocation, String aAssetsIndexCssLocation) {
        long now = System.currentTimeMillis();
        js = aAssetsIndexJsLocation + "?t=" + now;
        css = aAssetsIndexCssLocation + "?t=" + now;

    }

    @Override
    protected void doSafeGet(SafeServletRequest aRequest, SafeServletResponse aResponse) {

        aResponse.setContentType("text/html");

        aResponse.write(
                TEMPLATE
                        .replace("{{ ASSETS_INDEX_JS_URI }}" , js)
                        .replace("{{ ASSETS_INDEX_CSS_URI }}", css)
        );
    }

    @Override
    protected void doSafePost(SafeServletRequest aRequest, SafeServletResponse aResponse) {
        throw new IllegalStateException("No handler for " + aRequest.getRequestUrl());
    }

    private static final String TEMPLATE = """
            <!doctype html>
            <html lang="en">
            <head>
                <title>Monit Collector UI</title>
                <meta name="viewport" content="width=device-width,initial-scale=1">
                <script defer src = "{{ ASSETS_INDEX_JS_URI }}"></script>
                <link        href = "{{ ASSETS_INDEX_CSS_URI }}" rel="stylesheet">
            </head>
            <body class="awsui-dark-mode" style="background-color: #131925">
                <div id="root"></div>
            </body>
            </html>
            """;
}
