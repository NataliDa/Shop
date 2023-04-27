package com.example.security.config;

import com.example.security.enumm.Role;
import com.example.security.service.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncode() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .requestMatchers("/admin").hasRole(Role.ADMIN.name())
                .requestMatchers("/",
                        "/authentication",
                        "/registration",
                        "/registrationAdmin",
                        "/error",
                        "/logout",
                        "/person account/product/search",
                        "/resources/**",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/product",
                        "/product/info/{id}",
                        "/product/search").permitAll()
                .anyRequest().hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                .and()
                .formLogin().loginPage("/authentication")
                .loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/person account", true)
                .failureUrl("/authentication?error")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/authentication");
        return http.build();
    }

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncode());
    }
}
