package it.gym.backend_gym.service;

import it.gym.backend_gym.dao.CorsoDAO;
import it.gym.backend_gym.dto.CorsoDTO;
import it.gym.backend_gym.dto.RecensioneCorsoDTO;
import it.gym.backend_gym.entity.Corso;
import it.gym.backend_gym.entity.RecensioneCorso;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CorsoService {

    private final CorsoDAO dao;

    public CorsoService(CorsoDAO dao) {
        this.dao = dao;
    }

    public List<CorsoDTO> findAll() {
        return dao.findAll().stream().map(this::toDto).toList();
    }

    public CorsoDTO create(String titolo, String descrizione, String lezione, String fotoUrl) {
        String t = requireText(titolo, "Titolo");
        String d = requireText(descrizione, "Descrizione");
        String l = requireText(lezione, "Lezione");

        if (dao.existsByTitoloIgnoreCase(t)) {
            throw new ResponseStatusException(CONFLICT, "Esiste gia' un corso con questo titolo");
        }

        String cleanFoto = cleanUrl(fotoUrl);

        Corso c = new Corso(t, d, l);
        c.setFotoUrl(cleanFoto);

        Corso saved = dao.create(c);
        return toDto(saved);
    }

    public CorsoDTO update(long id, String titolo, String descrizione, String lezione, String fotoUrl) {
        Corso c = dao.findById(id);
        if (c == null) {
            throw new ResponseStatusException(NOT_FOUND, "Corso non trovato: " + id);
        }

        String t = requireText(titolo, "Titolo");
        String d = requireText(descrizione, "Descrizione");
        String l = requireText(lezione, "Lezione");

        if (dao.existsByTitoloIgnoreCaseAndIdNot(t, c.getId())) {
            throw new ResponseStatusException(CONFLICT, "Esiste gia' un corso con questo titolo");
        }

        c.setTitolo(t);
        c.setDescrizione(d);
        c.setLezione(l);
        c.setFotoUrl(cleanUrl(fotoUrl));

        return toDto(dao.update(c));
    }

    public void delete(long id) {
        if (!dao.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Corso non trovato: " + id);
        }
        if (dao.existsInIscrizioni(id)) {
            throw new ResponseStatusException(CONFLICT, "Corso iscritto da un cliente");
        }
        dao.delete(id);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " obbligatorio.");
        }
        return value.trim();
    }

    private String cleanUrl(String url) {
        if (url == null) return null;
        String trimmed = url.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private CorsoDTO toDto(Corso c) {
        List<RecensioneCorsoDTO> recensioni = c.getRecensioni()
                .stream()
                .map(this::toRecensioneDto)
                .toList();
        return new CorsoDTO(c.getId(), c.getTitolo(), c.getDescrizione(), c.getLezione(), c.getFotoUrl(), recensioni);
    }

    private RecensioneCorsoDTO toRecensioneDto(RecensioneCorso r) {
        String corsoTitolo = r.getCorso() != null ? r.getCorso().getTitolo() : null;
        String utenteEmail = r.getUtente() != null ? r.getUtente().getEmail() : null;
        Long corsoId = r.getCorso() != null ? r.getCorso().getId() : null;
        Long utenteId = r.getUtente() != null ? r.getUtente().getId() : null;
        return new RecensioneCorsoDTO(
                r.getId(),
                corsoId,
                corsoTitolo,
                utenteId,
                utenteEmail,
                r.getTesto(),
                toIso(r.getCreatedAt()),
                toIso(r.getUpdatedAt())
        );
    }

    private String toIso(LocalDateTime dateTime) {
        return Objects.requireNonNull(dateTime, "dateTime")
                .truncatedTo(ChronoUnit.MILLIS)
                .toString();
    }
}



