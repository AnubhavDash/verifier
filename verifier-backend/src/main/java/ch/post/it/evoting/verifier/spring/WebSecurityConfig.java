package ch.post.it.evoting.verifier.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
        auth.inMemoryAuthentication()
                .withUser(clientUsername).password(clientPassword).roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/socket/**").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic().authenticationEntryPoint(authEntryPoint);
    }
}