package com.lwh147.rtms.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @description: http请求封装类
 * @author: lwh
 * @create: 2021/5/5 12:23
 * @version: v1.0
 **/
public class RequestUtil extends Thread {
    /**
     * 请求方式
     **/
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String HTTP_METHOD_PUT = "PUT";
    /**
     * 一些关键字
     **/
    public static final String PROTOCOL_HTTP = "http";
    public static final String DEFAULT_ENCODING = "utf-8";
    public static final String REQUEST_PROPERTY_CONTENT_TYPE = "Content-Type";
    /**
     * 一些分隔符
     **/
    private static final String URL_SPLIT_CHARACTOR_AND = "&";
    private static final String URL_SPLIT_CHARACTOR_QUERY = "?";
    private static final String URL_SPLIT_CHARACTOR_EQUAL = "=";
    private static final String URL_SPLIT_CHARACTOR_SLASH = "/";
    /**
     * 请求体格式
     **/
    public static final String JSON = "json";
    public static final String XML = "xml";
    /**
     * 请求体格式编码
     **/
    public static final String DEFAULT_CONTENT_TYPE_JSON_UTF8 = "application/json;charset=utf-8";
    public static final String DEFAULT_CONTENT_TYPE_XML_UTF8 = "application/xml;charset=utf-8";

    private RequestUtil() {
    }

    /**
     * http Post请求
     *
     * @param api        请求url
     * @param data       发送到服务器的数据，使用json序列化的字符串
     * @param conentType 数据类型 输入xml或者json
     * @return java.lang.String
     **/
    public static String Post(String api, String data, String conentType) throws IOException {
        URL url = new URL(String.valueOf(api));
        //打开和url之间的连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        PrintWriter out;
        //请求方式
        conn.setRequestMethod(HTTP_METHOD_POST);
        //设置通用的请求属性
        if (JSON.equals(conentType)) {
            conn.setRequestProperty(REQUEST_PROPERTY_CONTENT_TYPE, DEFAULT_CONTENT_TYPE_JSON_UTF8);

        } else {
            conn.setRequestProperty(REQUEST_PROPERTY_CONTENT_TYPE, DEFAULT_CONTENT_TYPE_XML_UTF8);

        }
        //设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
        //最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
        //post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
        conn.setDoOutput(true);
        conn.setDoInput(true);
        //获取URLConnection对象对应的输出流
        out = new PrintWriter(conn.getOutputStream());
        //发送请求参数即数据
        out.print(data);
        //缓冲数据
        out.flush();
        //获取URLConnection对象对应的输入流
        InputStream is = conn.getInputStream();
        //构造一个字符流缓存
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String str = "";
        StringBuilder ans = new StringBuilder();
        while ((str = br.readLine()) != null) {
            ans.append(str);
        }
        //关闭流
        is.close();
        //断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
        //固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
        conn.disconnect();
        return ans.toString();
    }

    /**
     * get请求
     *
     * @param api    请求baseurl
     * @param params 请求参数，可以为null表示无参
     * @return java.lang.String
     **/
    public static String Get(String api, Map<String, String> params) throws IOException {
        URL url;
        if (params == null) {
            url = new URL(String.valueOf(api));
        } else {
            url = new URL(buildGetRequestUrlWithParams(api, params));
        }
        //打开和url之间的连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        PrintWriter out = null;
        //请求方式
        conn.setRequestMethod(HTTP_METHOD_GET);
        //设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
        //最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
        //post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
        conn.setDoOutput(false);
        conn.setDoInput(true);
        //获取URLConnection对象对应的输入流
        InputStream is = conn.getInputStream();
        //构造一个字符流缓存
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String str = "";
        StringBuilder ans = new StringBuilder();
        while ((str = br.readLine()) != null) {
            ans.append(str);
        }
        //关闭流
        is.close();
        //断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
        //固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
        conn.disconnect();
        return ans.toString();
    }

    public static String Put() {
        return null;
    }

    public static String Delete() {
        return null;
    }

    /**
     * 默认使用json，编码utf-8发送请求的请求
     *
     * @param api  请求url
     * @param data 请求数据
     * @return java.lang.String
     **/
    public static String Post(String api, String data) throws IOException {
        return Post(api, data, JSON);
    }

    /**
     * 没有请求参数的get请求
     *
     * @param api
     * @return java.lang.String
     **/
    public static String Get(String api) throws IOException {
        return Post(api, null);
    }

    /**
     * 路径请求参数get请求
     *
     * @param api
     * @param pathVariable 路径参数
     * @return java.lang.String
     **/
    public static String Patch(String api, String pathVariable) throws IOException {
        return Get(api + URL_SPLIT_CHARACTOR_SLASH + pathVariable);
    }

    /**
     * 构建带有请求参数的get请求url
     *
     * @param api    请求baseurl
     * @param params 参数map
     * @return java.lang.String
     **/
    private static String buildGetRequestUrlWithParams(String api, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(api);
        sb.append(URL_SPLIT_CHARACTOR_QUERY);
        for (String key : params.keySet()) {
            sb.append(key).append(URL_SPLIT_CHARACTOR_EQUAL).append(params.get(key)).append(URL_SPLIT_CHARACTOR_AND);
        }
        return sb.toString();
    }

}
