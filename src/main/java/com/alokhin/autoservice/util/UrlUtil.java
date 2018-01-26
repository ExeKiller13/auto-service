package com.alokhin.autoservice.util;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;

public class UrlUtil {

    public static String getContextPath(HttpServletRequest request) throws MalformedURLException {
        return getBasePath(request) + request.getContextPath();
    }

    public static String getBasePath(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort();
    }
}
