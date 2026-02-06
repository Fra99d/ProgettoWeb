package it.gym.backend_gym.config;

import it.gym.backend_gym.dao.UtenteDAO;
import it.gym.backend_gym.entity.Ruolo;
import it.gym.backend_gym.entity.Utente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    private final UtenteDAO utenteDAO;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UtenteDAO utenteDAO, PasswordEncoder passwordEncoder) {
        this.utenteDAO = utenteDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.trim().isEmpty()) return;
        if (adminPassword == null || adminPassword.isEmpty()) return;

        String email = adminEmail.trim().toLowerCase();
        if (utenteDAO.existsByEmail(email)) return;

        Utente admin = new Utente(email, passwordEncoder.encode(adminPassword), Ruolo.ADMIN);
        utenteDAO.create(admin);
    }
}
