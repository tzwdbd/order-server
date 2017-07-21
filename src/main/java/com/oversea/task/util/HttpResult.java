package com.oversea.task.util;

import java.io.UnsupportedEncodingException;

import javax.mail.Header;

import org.apache.http.cookie.Cookie;

/**
 * @version V1.0
 * @Title: HttpResult.java
 * @date 2013-11-25 下午6:48:59
 */
public class HttpResult {

    private static final String CODE = "UTF-8";

    private byte[] responseBody;

    private Cookie[] cookies;

    private String curUrl;

    private Header[] reqHeaders;

    private Header[] responseHeaders;

    private String queryStr;

    public String getCookiesStr() {
        if (cookies == null) {
            return null;
        }
        StringBuilder cookiesStr = new StringBuilder();
        for (Cookie cookie : cookies) {
            cookiesStr.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }
        return cookiesStr.toString();
    }

    public String getHtml() {
        return getHtml(CODE);
    }

    public String getHtml(String code) {
        try {
            return new String(responseBody, code);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    /**
     * @return the curUrl
     */
    public String getCurUrl() {
        return curUrl;
    }

    /**
     * @param curUrl the curUrl to set
     */
    public void setCurUrl(String curUrl) {
        this.curUrl = curUrl;
    }

    public Header[] getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(Header[] reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }
}
