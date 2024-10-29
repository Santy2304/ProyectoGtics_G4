package com.example.proyectogrupo4_gtics.Config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class ImpersonationAuthToken extends UsernamePasswordAuthenticationToken {

    public ImpersonationAuthToken(UserDetails userDetails) {
        super(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
