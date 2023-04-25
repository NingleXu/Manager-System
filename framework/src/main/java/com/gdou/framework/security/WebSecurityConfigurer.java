package com.gdou.framework.security;

import com.gdou.framework.security.filter.CaptchaFilter;
import com.gdou.framework.security.filter.JWTAuthenticationFilter;
import com.gdou.framework.security.handler.AuthenticationEntryPointImpl;
import com.gdou.framework.security.handler.LoginFailHandler;
import com.gdou.framework.security.handler.LoginSuccessHandler;
import com.gdou.framework.security.handler.LogoutSuccessHandler;
import com.gdou.framework.security.password.PasswordLoginFilter;
import com.gdou.framework.security.password.PasswordLoginProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import static com.gdou.common.constant.UrlConstants.*;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    /**
     * 密码登录提供
     */
    @Autowired
    private PasswordLoginProvider passwordLoginProvider;

    /**
     * 登录成功处理
     */
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    /**
     * 登录失败处理
     */
    @Autowired
    private LoginFailHandler loginFailHandler;

    /**
     * 退出登录成功后处理
     */
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CaptchaFilter captchaFilter;


    /**
     * 认证失败处理
     */
    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    /**
     * @author xzh
     * @time 2022/9/12 11:41
     * 用于获取自定义的AuthenticationManager
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * @author xzh
     * @time 2022/9/12 12:59
     * 构造注入Email登录
     */
    @Bean
    public PasswordLoginFilter passwordLoginFilter() throws Exception {
        PasswordLoginFilter passwordLoginFilter = new PasswordLoginFilter(authenticationManagerBean());
        passwordLoginFilter.setAuthenticationFailureHandler(loginFailHandler);
        passwordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        return passwordLoginFilter;
    }


    /**
     * @author xzh
     * @time 2022/9/12 13:02
     * 用于添加新的认证方式
     */
    @Override
    public void configure(AuthenticationManagerBuilder builder) {
        builder.authenticationProvider(passwordLoginProvider);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                //不创建session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class)
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(captchaFilter, JWTAuthenticationFilter.class);
    }

    /**
     * 不进行安全校验的白名单
     */
    public static final String[] URL_WHITELIST = {
            LOGIN_URL, //登录URL
            CAPTCHA_URL,//验证码URL
    };
}