package it.gym.backend_gym.service;

import it.gym.backend_gym.dao.AbbonamentoAttivoDAO;
import it.gym.backend_gym.dao.UtenteDAO;
import it.gym.backend_gym.dto.UtenteDTO;
import it.gym.backend_gym.entity.AbbonamentoAttivo;
import it.gym.backend_gym.entity.Ruolo;
import it.gym.backend_gym.entity.Utente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UtenteService {

    private final UtenteDAO utenteDAO;
    private final PasswordEncoder passwordEncoder;
    private final AbbonamentoAttivoDAO abbonamentoAttivoDAO;
    private final ClienteService clienteService;

    public UtenteService(UtenteDAO utenteDAO, PasswordEncoder passwordEncoder, AbbonamentoAttivoDAO abbonamentoAttivoDAO, ClienteService clienteService) {
        this.utenteDAO = utenteDAO;
        this.passwordEncoder = passwordEncoder;
        this.abbonamentoAttivoDAO = abbonamentoAttivoDAO;
        this.clienteService = clienteService;
    }

    public List<UtenteDTO> findAll() {
        return utenteDAO.findAll().stream().map(u -> new UtenteDTO(
                u.getId(),
                u.getEmail(),
                u.getRuolo().name(),
                hasActiveAbbonamento(u.getId())
        )).toList();
    }

    public UtenteDTO update(long id, String email, String password, String ruolo) {
        Utente u = utenteDAO.findById(id);
        if (u == null) {
            throw new ResponseStatusException(NOT_FOUND, "Utente non trovato: " + id);
        }

        if (email != null && !email.trim().isEmpty()) {
            String e = email.trim().toLowerCase();
            if (!e.contains("@")) {
                throw new ResponseStatusException(BAD_REQUEST, "Email non valida");
            }
            if (utenteDAO.existsByEmailAndIdNot(e, id)) {
                throw new ResponseStatusException(CONFLICT, "Email gia' presente");
            }
            u.setEmail(e);
        }

        if (password != null && !password.isEmpty()) {
            if (password.length() < 6) {
                throw new ResponseStatusException(BAD_REQUEST, "Password troppo corta (min 6)");
            }
            u.setPasswordHash(passwordEncoder.encode(password));
        }

        if (ruolo != null && !ruolo.isEmpty()) {
            try {
                u.setRuolo(Ruolo.valueOf(ruolo.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(BAD_REQUEST, "Ruolo non valido");
            }
        }

        Utente updated = utenteDAO.update(u);
        return new UtenteDTO(updated.getId(), updated.getEmail(), updated.getRuolo().name(), hasActiveAbbonamento(updated.getId()));
    }

    public Utente updateSelf(long id, String email, String password) {
        Utente u = utenteDAO.findById(id);
        if (u == null) {
            throw new ResponseStatusException(NOT_FOUND, "Utente non trovato: " + id);
        }

        if (email != null && !email.trim().isEmpty()) {
            String e = email.trim().toLowerCase();
            if (!e.contains("@")) {
                throw new ResponseStatusException(BAD_REQUEST, "Email non valida");
            }
            if (utenteDAO.existsByEmailAndIdNot(e, id)) {
                throw new ResponseStatusException(CONFLICT, "Email gia' presente");
            }
            u.setEmail(e);
        }

        if (password != null && !password.isEmpty()) {
            if (password.length() < 6) {
                throw new ResponseStatusException(BAD_REQUEST, "Password troppo corta (min 6)");
            }
            u.setPasswordHash(passwordEncoder.encode(password));
        }

        return utenteDAO.update(u);
    }

    public void delete(long id) {
        Utente u = utenteDAO.findById(id);
        if (u == null) {
            throw new ResponseStatusException(NOT_FOUND, "Utente non trovato: " + id);
        }
        utenteDAO.deleteById(id);
    }

    private boolean hasActiveAbbonamento(long utenteId) {
        AbbonamentoAttivo a = abbonamentoAttivoDAO.findByUtenteId(utenteId);
        if (a == null) return false;
        if (a.getEndDate() != null && a.getEndDate().isAfter(LocalDateTime.now())) {
            return true;
        }
        clienteService.rimuoviAbbonamento(utenteId);
        return false;
    }
}
