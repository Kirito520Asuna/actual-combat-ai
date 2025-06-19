package com.actual.combat.basic.im.core.utils;

import com.actual.combat.basic.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.type.Alias;


import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author minimalism
 * @Date 2023/8/11 0011 16:48
 * @Description
 */
@Alias("ImResponseUtils")
public class ResponseUtils {
    /**
     *
     * @param response
     * @throws IOException
     */

    public static PrintWriter result(HttpServletResponse response, String contentType, String encode) throws IOException {
        response.setContentType(contentType); // 设置响应内容类型为纯文本
        response.setCharacterEncoding(encode);//设置编码
        PrintWriter out = response.getWriter();
        return out;
    }

    public static PrintWriter resultUTF_8(HttpServletResponse response,String contentType) throws IOException {
        PrintWriter out = result(response,contentType,"UTF-8");
        return out;
    }

    public static PrintWriter resultJson(HttpServletResponse response,String encode) throws IOException {
        PrintWriter out = result(response,"application/json",encode);
        return out;
    }
    public static PrintWriter resultJsonUTF_8(HttpServletResponse response) throws IOException {
        PrintWriter out = result(response,"application/json","UTF-8");
        return out;
    }
    public static void response(HttpServletResponse response,Integer code,String msg) throws IOException {
//        response.setContentType("application/json"); // 设置响应内容类型为纯文本
//        response.setCharacterEncoding("UTF-8");
//        PrintWriter out = response.getWriter();

        PrintWriter out = resultJsonUTF_8(response);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(out, Result.result(code,msg,"")); // 将fail对象序列化为JSON字符串并写入输出流
//        out.close();
    }

    public static void responseException(HttpServletResponse response,String msg) throws IOException {
//        response.setContentType("application/json"); // 设置响应内容类型为纯文本
//        response.setCharacterEncoding("UTF-8");
//        PrintWriter out = response.getWriter();

        PrintWriter out = resultJsonUTF_8(response);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(out, Result.fail(msg)); // 将fail对象序列化为JSON字符串并写入输出流
//        out.close();
    }
}
