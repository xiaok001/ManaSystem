package com.janko.controller.sys;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.gson.Gson;
import com.janko.entity.SysLogEntity;
import com.janko.entity.sys.SysUserEntity;
import com.janko.service.RedisService;
import com.janko.service.SysLogService;
import com.janko.service.impl.AbstractBaseRedisService;
import com.janko.service.sys.SysUserRoleService;
import com.janko.service.sys.SysUserService;
import com.janko.utils.R;
import com.janko.utils.ShiroUtils;

/**
 * 登录相关
 * 
 * @author janko
 * @email 
 * @date 2016年11月10日 下午1:15:31
 */
@Controller
public class SysLoginController extends AbstractBaseRedisService<String, Object>{
	@Autowired
	private Producer producer;
	
	private static final Logger LOGGER = Logger.getLogger(SysLoginController.class);
	@RequestMapping("captcha.jpg")
	public void captcha(HttpServletResponse response)throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        
        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        //保存到shiro session
        ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, text);
        
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
	}
	
	/**
	 * 登录
	 * @throws InterruptedException 
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/login", method = RequestMethod.POST)
	public R login(String username, String password, String captcha)throws IOException, InterruptedException {
		String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
		//TODO: 取消登陆验证码的验证
		/*if(!captcha.equalsIgnoreCase(kaptcha)){
			return R.error(501,"验证码不正确");
		}*/
		//TestFun();
		
		//多数据源测试
		//multiDataSourceTest();
		try{
			Subject subject = ShiroUtils.getSubject();
			//sha256加密
			password = new Sha256Hash(password).toHex();
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			subject.login(token);
		}catch (UnknownAccountException e) {
			return R.error(e.getMessage());
		}catch (IncorrectCredentialsException e) {
			return R.error(e.getMessage());
		}catch (LockedAccountException e) {
			return R.error(e.getMessage());
		}catch (AuthenticationException e) {
			return R.error(404,"账户验证失败");
		}
	    
		return R.ok();
	}
	
	/**
	 * 退出
	 */
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout() {
		ShiroUtils.logout();
		return "redirect:login.html";
	}
	
	@Autowired
	RedisService redisService;
	
	public void TestFun(){
		/*↓↓↓↓↓↓该部分是redis的取值↑↑↓↓↓↓↓↓*/
		String carGpsInfo = (String) redisService.getObj("abc", carCurrentRedisTemplate);// DB0																			// 事实位置信息
		/* 判断redis0中的车辆实时数据 */
		if (!StringUtils.isEmpty(carGpsInfo)) {
			Gson gs = new Gson();
		}
		/*↑↑↑↑↑↑↑↑该部分是redis的取值↑↑↑↑↑↑↑↑*/
		
		/*↓↓↓↓↓↓该部分是redis的设值↑↑↓↓↓↓↓↓*/
		
		for (int i = 0; i < 10; i++) {
			redisService.set("key"+i, "value"+i, carCurrentRedisTemplate);
		}
		/*↑↑↑↑↑↑↑↑该部分是redis的设值↑↑↑↑↑↑↑↑*/
		redisService.delete("keys", carCurrentRedisTemplate);
		List<SysUserEntity> am=new ArrayList<SysUserEntity>();
		SysUserEntity user=new SysUserEntity();
		user.setCreateTime(new java.util.Date());
		user.setCreateUserId(1L);
		user.setEmail("123");
		user.setMobile("18601235468");
		user.setPassword("123");
		user.setUsername("name");
		am.add(user);
		/*redisService.addObjectList(am, carCurrentRedisTemplate);*/
		
		
		List<String> lst=new ArrayList<String>();
		for (int i = 1; i < 8; i++) {
			lst.add("key"+i);
		}
		redisService.delete(lst, carCurrentRedisTemplate);
	}
	
	@Autowired
	private SysUserService userService;

	@Autowired
	private SysLogService sysAdminService;
	
	public void multiDataSourceTest() {
		try {
			List<SysUserEntity> userList = userService.queryList(new HashMap<String, Object>());
			System.out.println("======>userService的个数是："+userList.size());
			List<SysLogEntity> adminList = sysAdminService.queryList(new HashMap<String,Object>());
			System.out.println("======>sysAdminService的个数是："+adminList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
