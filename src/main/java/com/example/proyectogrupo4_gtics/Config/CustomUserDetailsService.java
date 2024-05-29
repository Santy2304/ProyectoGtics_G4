package com.example.proyectogrupo4_gtics.Config;

import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Patient;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;
import com.example.proyectogrupo4_gtics.Entity.SuperAdmin;
import com.example.proyectogrupo4_gtics.Repository.AdministratorRepository;
import com.example.proyectogrupo4_gtics.Repository.PatientRepository;
import com.example.proyectogrupo4_gtics.Repository.PharmacistRepository;
import com.example.proyectogrupo4_gtics.Repository.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public  class CustomUserDetailsService  implements UserDetailsService {

    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
    private PharmacistRepository pharmacistRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public UserDetails loadUserByEmailPassword(String email , String password) throws UsernameNotFoundException {
        // Primero busca en la tabla de admin_users
        Patient patient = patientRepository.buscarPatient(email , password);
        Administrator admin = administratorRepository.buscarAdmin(email , password) ;
        SuperAdmin superAdmin = superAdminRepository.buscarSuperAdmin(email , password) ;
        Pharmacist pharmacist = pharmacistRepository.buscarPharmacist(email, password);
        if(!(patient == null)) {
            if( ! patient.getState().equals("baneado")) {
                return new org.springframework.security.core.userdetails.User(
                        patient.getEmail(),
                        patient.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("PACIENTE")));
            }else{
                throw new UsernameNotFoundException("Esta Baneado");
            }
        }

        if(!(admin == null)) {
            if( ! patient.getState().equals("baneado")) {
                return new org.springframework.security.core.userdetails.User(
                        admin.getEmail(),
                        admin.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));
            }else{
                throw new UsernameNotFoundException("Esta Baneado");
            }
        }

        if(!(superAdmin == null)) {
                return new org.springframework.security.core.userdetails.User(
                        superAdmin.getEmail(),
                        superAdmin.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("SUPERADMIN")));
        }

        if(!(pharmacist == null)) {
            if( ! pharmacist.getState().equals("baneado")) {
                return new org.springframework.security.core.userdetails.User(
                        pharmacist.getEmail(),
                        pharmacist.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("FARMACISTA")));
            }else{
                throw new UsernameNotFoundException("Esta Baneado");
            }
        }

        // Si no se encuentra el usuario en ninguna tabla
        throw new UsernameNotFoundException("Usuario no encontrado");
    }



}
