package com.zhanyd.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.zhanyd.app.common.ApiResult;
import com.zhanyd.app.common.ResultEnum;
import com.zhanyd.app.common.WeixinHelper;
import com.zhanyd.app.common.util.HttpService;
import com.zhanyd.app.common.util.JwtUtils;
import com.zhanyd.app.common.util.MD5Generate;
import com.zhanyd.app.common.util.SendSms;
import com.zhanyd.app.common.util.StringHelp;
import com.zhanyd.app.model.IdentifyingCode;
import com.zhanyd.app.model.UserInfo;
import com.zhanyd.app.service.IdentifyingCodeService;
import com.zhanyd.app.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.*;


/**
 * Created by zhanyd@sina.com on 2018/2/13 0013.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;
    
    @Autowired
    IdentifyingCodeService identifyingCodeService;


    @RequestMapping("/login")
    public ApiResult<Map<String, Object>> login(String code,HttpServletRequest request){
    	logger.info("微信登录：");
    	logger.info("code = " + code);
    	String nickname = "";
		String headimgurl = "";
		String unionid = "qwe";
		
    	ApiResult<Map<String, Object>> apiResult = new ApiResult<Map<String, Object>>();
         
    	//获取tonken和openid
    	/*String getTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WeixinHelper.APP_ID + "&secret=" + WeixinHelper.CORPSECRET + "&code=" + code + "&grant_type=authorization_code";
    	String tokenContent = HttpService.post(getTokenUrl);
		logger.info("tokenContent = " + tokenContent);
		JSONObject jsonObject = JSONObject.parseObject(tokenContent);
		String accessToken = jsonObject.getString("access_token");
		String openid = jsonObject.getString("openid");
		logger.info("accessToken = " + accessToken + " openid = " + openid);
		
		//获取用户信息
		String getUserUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
		String userContent = HttpService.post(getUserUrl);
		logger.info("userContent = " + userContent);
		jsonObject = JSONObject.parseObject(userContent);
		String nickname = jsonObject.getString("nickname");
		String headimgurl = jsonObject.getString("headimgurl");
		String unionid = jsonObject.getString("unionid");
		logger.info("nickname = " + nickname + " unionid = " + unionid + " headimgurl = " + headimgurl);*/
		
       
        //判断该用户是否已经存在
        UserInfo userInfo = userService.selectByUnionid(unionid);
        if(userInfo == null){
        	userInfo = new UserInfo();
        	userInfo.setNickName(nickname);
        	userInfo.setHeadimgurl(headimgurl);
        	userInfo.setUnionid(unionid);
        	userInfo.setCreateTime(new Date());
        	userService.insertSelective(userInfo);
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //生成token
        String token = JwtUtils.signJWT(userInfo.getId());
        if(token == null){
        	resultMap.put("msg", "生成token失败");
            return apiResult.fail(resultMap);
        }else{
            //logger.info(unionid + " 登录成功");
        	resultMap.put("userInfo", userInfo);
        	resultMap.put("token", token);
            return apiResult.success(resultMap);
        }
    }
    
    
    /**
    * 发送验证码
    * @param tel
    * @return
     * @throws ClientException 
    */
   @RequestMapping("/sendIdentifyingCode")
   public ApiResult<String> sendIdentifyingCode(String tel) throws ClientException {
       ApiResult<String> apiResult = new ApiResult<String>();
       Random random = new Random();
       String identifyingCode = random.nextInt(10) + "" + random.nextInt(10) + "" +random.nextInt(10) + "" +random.nextInt(10);

       //apiResult = SendSms.sendSms("15088923179","SMS_585014","{\"code\":\"1234\",\"product\":\"alidayu\"}");
       //插入验证码记录
       IdentifyingCode IdentifyingCode = new IdentifyingCode();
       IdentifyingCode.setTel(tel);
       IdentifyingCode.setIdentifyingCode(identifyingCode);
       IdentifyingCode.setValidTime(60);
       IdentifyingCode.setIsValid(true);
       IdentifyingCode.setCreateTime(new Date());
       identifyingCodeService.insertSelective(IdentifyingCode);
       
       
       logger.info("验证码发送成功：" + tel + " " + identifyingCode);
       //发送验证码
       return apiResult.success("验证码发送成功：" + identifyingCode);
   }

   
   @RequestMapping("/bindingTel")
   public ApiResult<String> bindingTel(Integer userId,String tel){
	   ApiResult<String> apiResult = new ApiResult<String>();
	   
	   return apiResult;
   }

}
