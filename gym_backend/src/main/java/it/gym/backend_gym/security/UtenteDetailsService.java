package it.gym.backend_gym.security;

import it.gym.backend_gym.dao.UtenteDAO;
import it.gym.backend_gym.entity.Utente;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtenteDetailsService implements UserDetailsService {

    private final UtenteDAO utenteDAO;

    public UtenteDetailsService(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = (username == null) ? "" : username.trim().toLowerCase();
        Utente u = utenteDAO.findByEmail(email);
        if (u == null) {
            throw new UsernameNotFoundException("Utente non trovato: " + email);
        }
        return new SecurityUser(u);
    }
}
