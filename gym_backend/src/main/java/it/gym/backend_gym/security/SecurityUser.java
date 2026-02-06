package it.gym.backend_gym.security;

import it.gym.backend_gym.entity.Ruolo;
import it.gym.backend_gym.entity.Utente;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private final Utente utente;

    public SecurityUser(Utente utente) {
        this.utente = utente;
    }

    public Long getId() {
        return utente.getId();
    }

    public Ruolo getRuolo() {
        return utente.getRuolo();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + utente.getRuolo().name()));
    }

    @Override
    public String getPassword() {
        return utente.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return utente.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
