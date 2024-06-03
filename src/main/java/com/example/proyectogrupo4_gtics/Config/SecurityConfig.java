package com.example.proyectogrupo4_gtics.Config;

import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Patient;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;
import com.example.proyectogrupo4_gtics.Entity.SuperAdmin;
import com.example.proyectogrupo4_gtics.Repository.AdministratorRepository;
import com.example.proyectogrupo4_gtics.Repository.PatientRepository;
import com.example.proyectogrupo4_gtics.Repository.PharmacistRepository;
import com.example.proyectogrupo4_gtics.Repository.SuperAdminRepository;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
    public SecurityFilterChain filterChain (HttpSecurity http,
                                            SuperAdminRepository superAdminRepository,
                                            AdministratorRepository administratorRepository,
                                            PharmacistRepository pharmacistRepository,
                                            PatientRepository patientRepository) throws Exception{
        http.formLogin()
                .loginPage("/inicioSesion")
                .loginProcessingUrl("/processLogin")
                .usernameParameter("email")
                .passwordParameter("password")
                //.failureHandler()
                .successHandler((request, response, authentication) -> {

                    HttpSession session = request.getSession();
                    String rol = "";
                    for(GrantedAuthority role : authentication.getAuthorities()){
                        rol = role.getAuthority();
                        break;
                    }
                    switch(rol){
                        case ("superadmin") :

                            session.setAttribute("usuario",superAdminRepository.findByEmail(authentication.getName()));
                            response.sendRedirect("/superAdmin/verListados");
                            break;
                        case ("admin"):
                            session.setAttribute("usuario",administratorRepository.findByEmail(authentication.getName()));
                            response.sendRedirect("/adminSede/sessionAdmin?idUser="+((Administrator) session.getAttribute("usuario")).getIdAdministrador());
                            break;
                        case ("farmacista"):
                            session.setAttribute("usuario",pharmacistRepository.findByEmail(authentication.getName()));
                            response.sendRedirect("/pharmacist/sessionPharmacist?idUser="+((Pharmacist) session.getAttribute("usuario")).getIdFarmacista());
                            break;
                        case ("paciente"):
                            session.setAttribute("usuario",(patientRepository.findByEmail(authentication.getName()).get()));
                            if(patientRepository.findByEmail(authentication.getName()).get().getChangePassword().equals(false)){
                                response.sendRedirect("/patient/cambioObligatorio?idUser="+ ((Patient) session.getAttribute("usuario")).getIdPatient());
                            }else{
                                response.sendRedirect("/patient/sessionPatient?idUser="+ ((Patient) session.getAttribute("usuario")).getIdPatient());
                            }
                            break;
                    }
                });
                http
                .csrf().disable();

        http.authorizeHttpRequests()
                .requestMatchers("/patient/**" ,"/patient" ).hasAnyAuthority("paciente","superadmin")
                .requestMatchers("/adminSede/**" , "/adminSede" ).hasAnyAuthority("admin","superadmin")
                .requestMatchers("/pharmacist/**" , "/pharmacist" ).hasAnyAuthority("farmacista", "superadmin")
                .requestMatchers("/superAdmin/**" ,"/superAdmin").hasAuthority("superadmin")
                .anyRequest().permitAll();


        http.logout()
                .logoutSuccessUrl("/inicioSesion")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

        return http.build();

    }

}
