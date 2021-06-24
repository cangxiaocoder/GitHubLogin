package com.cx.controller;

import com.cx.pojo.User;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author 苍晓
 * @Date 2020/3/20 13:37
 */
@Controller
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    //从哪儿登录回到那个页面
    private static String returnUrl = "https://www.xxx.com";
    /**
     * @Description: 登录入口,发起qq登录
     * @Param request
     * @Param response
     * @DATE 2020/3/22 13:40
     * @return {@link ResponseEntity<Void>}
     */
    @GetMapping("/login")
    public ResponseEntity<Void> login(HttpServletRequest request, HttpServletResponse response){
        returnUrl = request.getParameter("returnUrl");
        response.setContentType("text/html,charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
            return ResponseEntity.ok().build();
        } catch (IOException | QQConnectException e) {
            LOGGER.warn("请求QQ登录失败, {}",e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping("/qqlogin")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model){

        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken   = null;
            String openID = null;
            long tokenExpireIn = 0L;
            if (accessTokenObj.getAccessToken().equals("")) {
                LOGGER.info("没有获取到响应参数：{}",accessTokenObj);
            }else {
                accessToken = accessTokenObj.getAccessToken();
                LOGGER.info("用户token==》{}",accessToken);
                tokenExpireIn = accessTokenObj.getExpireIn();
                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                UserInfo userInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = userInfo.getUserInfo();
                if (userInfoBean.getRet()==0){
                    User user = new User();
                    //QQ空间头像
                    user.setAvatar(userInfoBean.getAvatar().getAvatarURL50());
                    //唯一标识
                    user.setOpenId(openID);
                    //qq昵称
                    user.setNickName(userInfoBean.getNickname());
                    //性别
                    user.setGender(userInfoBean.getGender());
                    model.addAttribute("user",user);
                }else {
                    LOGGER.warn("很抱歉，我们没能正确获取到您的信息，原因是：{} ", userInfoBean.getMsg());
                }
            }

        } catch (QQConnectException e) {
            LOGGER.error("qq连接发生异常 {}",e.getMessage());
        }
        return "index.html";
    }
}
