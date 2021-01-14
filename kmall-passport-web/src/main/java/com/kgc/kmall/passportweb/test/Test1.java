package com.kgc.kmall.passportweb.test;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shkstart
 * @create 2021-01-14 17:16
 */
public class Test1 {
    public static void main(String[] args) {
        //根据授权码获取access_token
        String s3 = "https://api.weibo.com/oauth2/access_token";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","458725276");
        paramMap.put("client_secret","75076665135d78a58fe86d15e001fbd9");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.kmall.com:8086/vlogin");
        paramMap.put("code","f22f10f0b6d55178666524d80cb0bdfc");// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);
        Map<String,String> access_map = JSON.parseObject(access_token_json,Map.class);
        String access_token = access_map.get("access_token");
        String uid = access_map.get("uid");
        String s4 = "https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid;
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);
        System.out.println(user_map);
    }
}


