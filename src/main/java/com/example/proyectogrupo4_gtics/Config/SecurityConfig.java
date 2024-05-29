package com.example.proyectogrupo4_gtics.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //Sirve como filtro
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception{
        http.formLogin()
                .loginPage("/iniciarSesion")
                .loginProcessingUrl("/validarUsuario");

        /*
        http.authorizeHttpRequests()
                .requestMatchers("/employee","/employee/**").authenticated()
                .requestMatchers("/shipper","/shipper/**").authenticated()
                .anyRequest().permitAll();
        */

        return http.build();

    }

}
