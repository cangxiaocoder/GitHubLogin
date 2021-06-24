package com.cx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author cangxiao
 * @Date 2021/6/21
 * @Desc
 */
@Controller
public class GitHubLoginController {

    private static String CLIENT_ID = "929176a01d6391b9526d";
    private static String CLIENT_SECRET = "6db626f5f0fa7ff03ea5f8b8952bb66a8fe3d2ab";
    private static String REDIRECT_URL = "http://127.0.0.1:8080/githubCallback";

    @Autowired
    private RestTemplate skipRestTemplate;

    private String state;
    @GetMapping("/githubLogin")
    public String  login() throws IOException {
        System.out.println("========");
        // Github认证服务器地址
        String url = "https://github.com/login/oauth/authorize";
        // 随机字符串，用于防止CSRF攻击
        state = String.valueOf(UUID.randomUUID()).replaceAll("-","");
        // 传递参数
        String param = "client_id=" + CLIENT_ID + "&state=" + state + "&redirect_uri=" + REDIRECT_URL;
        //请求Github认证服务器
        url = url + "?" + param;
        return "redirect:"+url;
    }

    /**
     * GitHub回调方法
     * @param code 授权码
     * @param state 发送时的随机字符串
     */
    @GetMapping("/githubCallback")
    public String  githubCallback(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) throws Exception {
        //1 验证state，如果不一致，可能被CSRF攻击
        if(!this.state.equals(state)) {
            throw new Exception("State验证失败");
        }

        // 2、向GitHub认证服务器申请令牌
        String tokenUrl = "https://github.com/login/oauth/access_token";
        // 申请令牌，
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id",CLIENT_ID);
        paramMap.put("client_secret",CLIENT_SECRET);
        paramMap.put("code",code);

        String result = skipRestTemplate.postForObject(tokenUrl,paramMap,String.class);
        System.out.println(result);

        String userUrl = "https://api.github.com/user";

        //3 申请资源
        HttpHeaders headers = new HttpHeaders();
        //认证token放在报文头
        headers.set("Authorization", "token "+result.split("=")[1].split("&")[0]);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = skipRestTemplate.exchange(userUrl, HttpMethod.GET, httpEntity, String.class);
        // 4、输出用户信息
        String userInfo = responseEntity.getBody();
        System.out.println(userInfo);
        return "index.html";
    }
}
