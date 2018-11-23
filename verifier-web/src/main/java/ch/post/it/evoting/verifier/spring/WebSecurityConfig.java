package ch.post.it.evoting.verifier.spring;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;


@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${client.username}")
    private String clientUsername;

    @Value("${client.password}")
    private String clientPassword;

    @Autowired
    private AuthenticationEntryPoint authEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (StringUtils.isNotEmpty(clientUsername)) {
            auth.inMemoryAuthentication()
                    .withUser(clientUsername).password(clientPassword).roles("USER");
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/socket/**").permitAll();

        if (StringUtils.isEmpty(clientUsername)) {
            http.authorizeRequests().antMatchers("**").permitAll();
        } else {
            http.authorizeRequests().anyRequest().authenticated()
                    .and().httpBasic().authenticationEntryPoint(authEntryPoint);
        }

    }
}