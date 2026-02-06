package it.gym.backend_gym.controller;

import it.gym.backend_gym.dao.UtenteDAO;
import it.gym.backend_gym.entity.Ruolo;
import it.gym.backend_gym.entity.Utente;
import it.gym.backend_gym.security.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtenteDAO utenteDAO;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UtenteDAO utenteDAO, PasswordEncoder passwordEncoder) {
        this.utenteDAO = utenteDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public record RegisterRequest(String email, String password) {}
    public record AuthResponse(Long id, String email, String ruolo) {}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest req) {
        if (req == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Richiesta non valida");
        }
        String email = (req.email() == null) ? "" : req.email().trim().toLowerCase();
        String password = (req.password() == null) ? "" : req.password();

        if (email.isEmpty() || !email.contains("@")) {
            throw new ResponseStatusException(BAD_REQUEST, "Email non valida");
        }
        if (password.length() < 6) {
            throw new ResponseStatusException(BAD_REQUEST, "Password troppo corta (min 6)");
        }
        if (utenteDAO.existsByEmail(email)) {
            throw new ResponseStatusException(CONFLICT, "Email gia' registrata");
        }

        Utente u = new Utente(email, passwordEncoder.encode(password), Ruolo.CLIENTE);
        Utente created = utenteDAO.create(u);
        return new AuthResponse(created.getId(), created.getEmail(), created.getRuolo().name());
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken());
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse login(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenziali non valide");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SecurityUser user)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenziali non valide");
        }
        request.getSession(true);
        return new AuthResponse(user.getId(), user.getUsername(), user.getRuolo().name());
    }

    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Non autenticato");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SecurityUser user)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Non autenticato");
        }
        return new AuthResponse(user.getId(), user.getUsername(), user.getRuolo().name());
    }
}
