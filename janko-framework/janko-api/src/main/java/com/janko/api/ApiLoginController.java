package com.janko.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.janko.annotation.IgnoreAuth;
import com.janko.service.TokenService;
import com.janko.service.UserService;
import com.janko.utils.R;
import com.janko.validator.Assert;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * API登录授权
 *
 * @author janko
 * @email 
 * @date 2017-03-23 15:31
 */
@Api(value = "API登录授权")
@RestController
@RequestMapping("/api")
public class ApiLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;


    /**
     * 登录
     */
    @ApiOperation(value = "用户登陆接口实现", notes = "用户登陆接口实现",response=R.class)
    @IgnoreAuth
    @PostMapping("login")
    public R login(String mobile, String password){
        Assert.isBlank(mobile, "手机号不能为空");
        Assert.isBlank(password, "密码不能为空");

        //用户登录
        long userId = userService.login(mobile, password);

        //生成token
        Map<String, Object> map = tokenService.createToken(userId);

        return R.ok(map);
    }

}
