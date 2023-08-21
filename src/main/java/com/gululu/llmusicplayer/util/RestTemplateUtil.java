package com.gululu.llmusicplayer.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.gululu.llmusicplayer.entity.Setting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;
@Slf4j
public class RestTemplateUtil {
    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 
     * @param url 网络请求接口url
     * @param param 请求header参数
     * @return 数据json
     */
    public static JSONObject getJsonDataRestTemplate(String url,Map<String,Object> param){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        param.put("timestamp",new Date().getTime());
        HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(param), headers);
        try{
            ResponseEntity<String>  results = restTemplate.exchange(url, HttpMethod.POST, formEntity, String.class);
            log.info("网络请求成功，地址：{}",url);
            return JSONObject.parseObject(results.getBody());
        }catch (Exception e){
            log.info("网络连接错误，接口错误，地址：{}",url);
            log.info(String.valueOf(e));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",404);
            return jsonObject;
        }
    }
    public static String getUserOpenID(Setting setting, String code){
        String url = setting.getSeOpenIdUrl()+"&appid=" + setting.getSeAppId() + "&secret=" + setting.getSeAppSecret() + "&js_code=" + code;
        ResponseEntity<String> results = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String body = results.getBody();
        JSONObject json = JSONObject.parseObject(body);
        if (json != null && !json.containsKey("errcode") && !json.containsKey("errmsg")) {
            return json.getString("openid");
        }else {
            log.info("网络连接错误、接口错误，地址：{}",url);
            return null;
        }
    }
}
