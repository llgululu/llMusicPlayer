package com.gululu.llmusicplayer.interceptor;

import com.alibaba.fastjson.JSON;
import com.gululu.llmusicplayer.util.JWTUtil;
import com.gululu.llmusicplayer.util.R;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JWTInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String time = formatter.format(date);
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        if (hour==3&&minute>0&&minute<30){
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            String respStr = JSON.toJSONString(R.error(3, "系统维护中..."));
            response.getOutputStream().write(respStr.getBytes(StandardCharsets.UTF_8));
            return false;
        }else {
            // 如果是登陆则放行
            if (request.getRequestURI().contains("login") || request.getRequestURI().contains("error")) {
                return true;
            } else {
                String token = request.getHeader("token");// 从 http 请求头中取出 token
                // 没有提交token
                if (token == null) {
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json; charset=utf-8");
                    String respStr = JSON.toJSONString(R.error(1, "没有token，请重新登录"));
                    response.getOutputStream().write(respStr.getBytes(StandardCharsets.UTF_8));
                    return false;
                } else {
                    //token校验失败
                    R<Claims> result = JWTUtil.validateJWT(token);  //校验token
                    if (result.getCode() != 1) {
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json; charset=utf-8");
                        String respStr = JSON.toJSONString(R.error(2, result.getMsg()));
                        response.getOutputStream().write(respStr.getBytes(StandardCharsets.UTF_8));
                        return false;
                    } else {
                        String uid = (String) result.getObj().get("uId");
                        request.setAttribute("uid", uid);
                        return true;
                    }
                }

            }
        }
    }
}
