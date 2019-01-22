package com.relesee.service;

import com.alibaba.fastjson.JSON;
import com.relesee.dao.RolePermissionDao;
import com.relesee.domains.Result;
import com.relesee.domains.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ShiroService extends AuthorizingRealm {

    private static final Logger logger = Logger.getLogger(ShiroService.class);

    @Autowired
    UserService userService;

    @Autowired
    RolePermissionDao rolePermissionDao;

    /**
     * 授权查询回调函数，进行鉴权但缓存中无用户的授权信息时调用
     * 查询数据库中当前用户对应的权限以授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //System.out.println("-------------------------    授权    ----------------------");
        logger.info("用户登录成功，开始授权");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Set<String> permission = rolePermissionDao.selectPermissionByRole(user.getRole());
        Set<String> role = new HashSet<>();
        System.out.println(JSON.toJSONString(user));
        role.add(user.getRole());
        authorizationInfo.setStringPermissions(permission);
        authorizationInfo.setRoles(role);
        return authorizationInfo;
    }

    /**
     * 身份认证回调函数
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String userId = upToken.getUsername();
        String password = new String(upToken.getPassword());

        Result<User> result = userService.login(userId, password);
        SimpleAuthenticationInfo loginfo = null;
        if (result.isFlag()){
            //设置超时时间，这样设置可以使用户自定义超时时间,默认3小时
            SecurityUtils.getSubject().getSession().setTimeout(10800000);
            User user = result.getResult();
            //下面构造方法的第一个参数就是pricipal(subject.getPricipal)
            loginfo = new SimpleAuthenticationInfo(user, user.getPassword(), getName());
            //SecurityUtils.getSubject().getSession().setAttribute("subject", user);
        } else {
            throw new AuthenticationException();
        }
        return loginfo;
    }
}
