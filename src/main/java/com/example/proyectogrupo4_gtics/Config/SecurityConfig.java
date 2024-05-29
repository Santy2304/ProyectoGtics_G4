package com.example.proyectogrupo4_gtics.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig  {
    final DataSource dataSource;
    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsManager users(DataSource dataSource){
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        String sql1 = "SELECT email, password, state FROM users WHERE email = ?";
        String sql2 = "SELECT u.email, r.name FROM users u "
                + "INNER JOIN rol r ON (u.idRol = r.idRol) "
                + "WHERE u.email = ?";
        users.setUsersByUsernameQuery(sql1);
        users.setAuthoritiesByUsernameQuery(sql2);
        return users;
    }
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception{
        http.formLogin()
                .loginPage("/inicioSesion")
                .loginProcessingUrl("/processLogin")
                .usernameParameter("email")
                .passwordParameter("password")
                //.failureHandler()
                .successHandler((request, response, authentication) -> {
                    String rol = "";
                    for(GrantedAuthority role : authentication.getAuthorities()){
                        rol = role.getAuthority();
                        break;
                    }
                    switch(rol){
                        case ("superadmin") :
                            response.sendRedirect("/superAdmin/verListadosSuperAdmin");
                            break;
                        case ("admin"):
                            response.sendRedirect("/adminSede/sessionAdmin?idUser="+2);
                            break;
                        case ("farmacista"):
                            response.sendRedirect("/pharmacist/sessionPharmacist?idUser="+2);
                            break;
                        case ("paciente"):
                            response.sendRedirect("/patient/sessionPatient?idUser="+2);
                            break;
                    }

                });




        return http.build();

    }

}
