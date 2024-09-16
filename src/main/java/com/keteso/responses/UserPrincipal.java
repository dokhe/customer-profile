package com.keteso.responses;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserPrincipal implements UserDetails {
    private final PinDTO pinDTO;
    public UserPrincipal(PinDTO pinDTO) {
        this.pinDTO = pinDTO;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }
    @Override
    public String getPassword() {
        return null;
    }
    @Override
    public String getUsername() {
        return pinDTO.getIdentifier();
    }
    @Override
    public boolean isAccountNonExpired() {
        return pinDTO.getStatus() == 1;
    }
    @Override
    public boolean isAccountNonLocked() {
        return pinDTO.getStatus() == 1;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return pinDTO.getStatus() == 1;
    }
}
