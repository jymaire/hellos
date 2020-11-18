package org.lagonette.hellos.configuration;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final Dotenv dotenv;

    public WebSecurityConfig(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/helloasso/payment", "/style/*.css", "/images/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/list", true)
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login");
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        String users = dotenv.get("USERS");
        List<String> logins = Arrays.asList(users.split(","));
        String passwordsString = dotenv.get("PASSWORDS");
        List<String> passwords = Arrays.asList(passwordsString.split(","));
        Set<UserDetails> userDetails = new HashSet<>();
        for (int i = 0; i < logins.size(); i++) {
            userDetails.add(User.builder()
                    .username(logins.get(i))
                    .password(encoder().encode(passwords.get(i)))
                    .roles("USER")
                    .build());
        }
        return new InMemoryUserDetailsManager(userDetails);
    }
}
